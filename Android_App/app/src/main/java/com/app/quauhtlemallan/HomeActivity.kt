package com.app.quauhtlemallan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        val country = bundle?.getString("country")
        setup(email ?: "", provider ?: "")

        val prefs:SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_progress -> {
                    // Acción para Progreso
                    true
                }
                R.id.navigation_chat -> {
                    // Acción para Chat
                    true
                }
                R.id.navigation_home -> {
                    // Acción para Inicio
                    true
                }
                R.id.navigation_games -> {
                    // Acción para Juegos
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java).apply {
                        putExtra("email", email)
                        putExtra("provider", provider)
                        putExtra("country", country)
                    }
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"
    }

}