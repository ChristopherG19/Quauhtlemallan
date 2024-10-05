package com.app.quauhtlemallan.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.ProviderType
import com.app.quauhtlemallan.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hbb20.CountryCodePicker

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.title = "Registro"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Referencias a los elementos de la UI
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmPasswordEditText)
        val registerButton: Button = findViewById(R.id.registerButton)

        val auth = FirebaseAuth.getInstance()

        // Evento de clic para registrar con email y contraseña
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val countryCodePicker: CountryCodePicker = findViewById(R.id.ccp)
            val countryName = countryCodePicker.selectedCountryName

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Guardar información adicional en Realtime Database
                                val userId = auth.currentUser?.uid ?: ""
                                val database = FirebaseDatabase.getInstance()
                                val usersRef = database.getReference("usuarios")
                                val user = User(username, email, countryName)

                                usersRef.child(userId).setValue(user)
                                    .addOnSuccessListener {
                                        showHome(email, ProviderType.BASIC, countryName)
                                    }
                                    .addOnFailureListener {
                                        showAlert("Error", "No se pudo guardar la información del usuario.")
                                    }
                            } else {
                                showAlert("Error de Registro", "No se pudo registrar el usuario.")
                            }
                        }
                } else {
                    showAlert("Contraseñas no coinciden", "Por favor, verifica las contraseñas.")
                }
            } else {
                showAlert("Campos Vacíos", "Por favor, completa todos los campos.")
            }
        }
    }

    private fun showAlert(s: String, s1: String) {
        Toast.makeText(this, s + " : " + s1, Toast.LENGTH_SHORT).show()
    }

    private fun showHome(email: String, provider: ProviderType, country: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            putExtra("country", country)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)  // Inicia MainActivity y destruye el flujo anterior
    }


    override fun onResume() {
        // Referencias a los elementos de la UI
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmPasswordEditText)

        super.onResume()
        // Limpiar los campos de entrada
        usernameEditText.text.clear()
        emailEditText.text.clear()
        passwordEditText.text.clear()
        confirmPasswordEditText.text.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Acciones al presionar la flecha de regreso
                finish() // Cierra la actividad actual y regresa a la anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
