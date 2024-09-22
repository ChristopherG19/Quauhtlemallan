package com.app.quauhtlemallan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hbb20.CountryCodePicker
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val ccp = findViewById<CountryCodePicker>(R.id.ccp)

        val provider = intent.getStringExtra("provider")
        if (provider == ProviderType.FACEBOOK.name || provider == ProviderType.GOOGLE.name) {
            editTextPassword.isEnabled = false  // Deshabilita el campo de contraseña
        }

        // Obtener el userId de FirebaseAuth
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.child(userId).get().addOnSuccessListener { snapshot ->
            val email = snapshot.child("email").getValue(String::class.java) ?: "No disponible"
            val username = snapshot.child("username").getValue(String::class.java) ?: "No disponible"
            val country = snapshot.child("country").getValue(String::class.java) ?: "No disponible"

            // Mostrar los datos en los EditTexts y en el CountryCodePicker
            editTextEmail.setText(email)
            editTextUsername.setText(username)
            val countryCode = getCountryCodeByName(country)
            if (countryCode != "No disponible") {
                ccp.setCountryForNameCode(countryCode)  // Mostrar el país en el CountryCodePicker
            } else {
                Toast.makeText(this, "País no disponible", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener la información del usuario", Toast.LENGTH_SHORT).show()
        }

        val buttonSaveChanges = findViewById<Button>(R.id.buttonSaveChanges)
        buttonSaveChanges.setOnClickListener {
            // Guardar los cambios en Firebase Realtime Database
            val updatedUsername = editTextUsername.text.toString()
            val updatedEmail = editTextEmail.text.toString()
            val updatedCountry = ccp.selectedCountryName // Obtener el código del país seleccionado
            val newPassword = editTextPassword.text.toString()

            if (newPassword.isNotEmpty()) {
                val user = FirebaseAuth.getInstance().currentUser

                // Actualizar la contraseña del usuario
                user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show()
                }
            }

        }

        val logOutButton = findViewById<Button>(R.id.logOutButton)
        logOutButton.setOnClickListener {
            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            val provider = intent.getStringExtra("provider")
            if (provider == ProviderType.FACEBOOK.name) {
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

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
}
