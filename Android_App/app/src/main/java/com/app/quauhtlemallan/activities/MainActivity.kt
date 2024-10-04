package com.app.quauhtlemallan.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.quauhtlemallan.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recibir los extras del Intent desde RegisterActivity o SignInActivity
        val email = intent.getStringExtra("email")
        val provider = intent.getStringExtra("provider")
        val country = intent.getStringExtra("country")

        // Si hay datos, cargamos el HomeFragment con los argumentos
        if (savedInstanceState == null) {
            val homeFragment = HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("email", email)
                    putString("provider", provider)
                    putString("country", country)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit()
        }

        // Configura el BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_chat -> ChatBotFragment()
                R.id.navigation_settings -> SettingsFragment()
                R.id.navigation_progress -> ProgressFragment()
                R.id.navigation_games -> GamesFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }

        // Mostrar el fragmento por defecto (HomeFragment) y seleccionar el botón de "Inicio"
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

            // Seleccionar el ítem de "Inicio" en el BottomNavigationView manualmente
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }
    }
}
