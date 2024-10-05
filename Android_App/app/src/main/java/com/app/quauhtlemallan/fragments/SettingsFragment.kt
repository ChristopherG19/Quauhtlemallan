package com.app.quauhtlemallan.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.activities.StartActivity
import com.app.quauhtlemallan.data.ProviderType
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hbb20.CountryCodePicker
import java.util.Locale

class SettingsFragment : Fragment() {

    private val GALLERY_PIC_REQUEST = 200
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var ccp: CountryCodePicker
    private lateinit var uploadPhotoButton: Button
    private lateinit var logOutButton: Button
    private lateinit var profileImage: ImageView
    private var imageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImage = view.findViewById(R.id.profileImage)
        loadImageFromFirebase()

        activity?.title = "Ajustes"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inicializar vistas
        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        ccp = view.findViewById(R.id.ccp)
        logOutButton = view.findViewById(R.id.logOutButton)
        uploadPhotoButton = view.findViewById(R.id.buttonChangePhoto)

        // Obtener el proveedor de los argumentos
        val provider = arguments?.getString("provider")
        if (provider == ProviderType.FACEBOOK.name || provider == ProviderType.GOOGLE.name) {
            editTextPassword.isEnabled = false  // Deshabilita el campo de contraseña
        }

        // Obtener el userId de FirebaseAuth
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("usuarios")

        // Obtener los datos del usuario
        usersRef.child(userId).get().addOnSuccessListener { snapshot ->
            val email = snapshot.child("email").getValue(String::class.java) ?: "No disponible"
            val username = snapshot.child("username").getValue(String::class.java) ?: "No disponible"
            val country = snapshot.child("country").getValue(String::class.java) ?: "No disponible"

            // Mostrar los datos en los EditTexts y en el CountryCodePicker
            editTextEmail.setText(email)
            editTextUsername.setText(username)
            val countryCode = getCountryCodeByName(country)
            if (countryCode != "No disponible") {
                ccp.setCountryForNameCode(countryCode)
            } else {
                Toast.makeText(requireContext(), "País no disponible", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al obtener la información del usuario", Toast.LENGTH_SHORT).show()
        }

        // Botón para subir una foto
        uploadPhotoButton.setOnClickListener {
            openGallery()
        }

        // Botón de guardar cambios
        val buttonSaveChanges = view.findViewById<Button>(R.id.buttonSaveChanges)
        buttonSaveChanges.setOnClickListener {
            saveChanges(usersRef, userId)
        }

        // Botón de cerrar sesión
        logOutButton.setOnClickListener {
            logOut(provider)
        }

        return view
    }

    // Método para abrir la galería
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_PIC_REQUEST)
    }

    // Método para manejar el resultado de la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_PIC_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            imageUri = data.data
            imageUri?.let { uploadImageToFirebase(it) }
        }
    }

    // Subir imagen a Firebase Storage
    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageReference = FirebaseStorage.getInstance().getReference("UserImages").child("${userId}_profile_image")

        storageReference.putFile(imageUri).addOnSuccessListener {
            // Obtener el enlace de descarga
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                saveImageLinkToRealtimeDatabase(uri.toString(), userId)
                updateImageView(uri.toString())
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al obtener el enlace de la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Guardar el enlace de la imagen en Firebase Realtime Database
    private fun saveImageLinkToRealtimeDatabase(imageUrl: String, userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userId)
        databaseReference.child("profileImage").setValue(imageUrl).addOnSuccessListener {
            Toast.makeText(requireContext(), "Imagen guardada correctamente", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Error al guardar la imagen en la base de datos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para guardar los cambios
    private fun saveChanges(usersRef: DatabaseReference, userId: String) {
        val updatedUsername = editTextUsername.text.toString()
        val updatedEmail = editTextEmail.text.toString()
        val updatedCountry = ccp.selectedCountryName
        val newPassword = editTextPassword.text.toString()

        if (newPassword.isNotEmpty()) {
            val user = FirebaseAuth.getInstance().currentUser

            // Actualizar la contraseña del usuario
            user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val userUpdates = mapOf(
            "username" to updatedUsername,
            "email" to updatedEmail,
            "country" to updatedCountry
        )

        usersRef.child(userId).updateChildren(userUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateImageView(imageUrl: String) {
        val profileImage: ImageView = view?.findViewById(R.id.profileImage) ?: return
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.ic_default)
            .error(R.drawable.ic_default)
            .into(profileImage)
    }

    private fun loadImageFromFirebase() {
        val profileImage: ImageView = view?.findViewById(R.id.profileImage) ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userId)

        // Obtener el enlace de la imagen guardada en Firebase
        databaseReference.child("profileImage").get().addOnSuccessListener { snapshot ->
            val imageUrl = snapshot.getValue(String::class.java)

            if (!imageUrl.isNullOrEmpty()) {
                // Si el usuario tiene una imagen de perfil, cargarla usando Glide o Picasso
                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default) // Mostrar ic_default mientras se carga
                    .error(R.drawable.ic_default) // Si falla la carga, mostrar ic_default
                    .into(profileImage)
            } else {
                // Si no hay imagen de perfil, mostrar ic_default
                profileImage.setImageResource(R.drawable.ic_default)
            }
        }.addOnFailureListener {
            // Si ocurre un error al cargar los datos, mostrar ic_default
            profileImage.setImageResource(R.drawable.ic_default)
            Toast.makeText(requireContext(), "Error al cargar la imagen de perfil", Toast.LENGTH_SHORT).show()
        }
    }


    // Método para cerrar sesión
    private fun logOut(provider: String?) {
        val prefs: SharedPreferences.Editor = requireActivity().getSharedPreferences(
            getString(R.string.prefs_file), Context.MODE_PRIVATE
        ).edit()
        prefs.clear()
        prefs.apply()

        if (provider == ProviderType.FACEBOOK.name) {
            LoginManager.getInstance().logOut()
        }

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(requireContext(), StartActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    // Obtener el código del país a partir del nombre
    private fun getCountryCodeByName(countryName: String): String? {
        val countryList = Locale.getISOCountries()
        for (countryCode in countryList) {
            val locale = Locale("", countryCode)
            if (locale.displayCountry.equals(countryName, ignoreCase = true)) {
                return countryCode
            }
        }
        return null
    }

    companion object {
        // Método estático para crear un fragmento con los argumentos
        fun newInstance(provider: String): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putString("provider", provider)
            fragment.arguments = args
            return fragment
        }
    }
}

