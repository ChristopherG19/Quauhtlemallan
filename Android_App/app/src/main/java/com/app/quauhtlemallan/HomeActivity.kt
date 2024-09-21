package com.app.quauhtlemallan

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        val prefs:SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"

        val emailTextView: TextView = findViewById(R.id.emailTextView)
        val providerTextView: TextView = findViewById(R.id.providerTextView)
        val countryTextView: TextView = findViewById(R.id.countryTextView)
        val logoutBtn: Button = findViewById(R.id.logOutButton)

        emailTextView.text = email
        providerTextView.text = provider

        // Obtener información adicional del usuario desde Realtime Database
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.child(userId).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                countryTextView.text = "País: ${user.country}"
            } else {
                countryTextView.text = "País: No disponible"
            }
        }.addOnFailureListener {
            countryTextView.text = "Error al obtener el país"
        }

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

}