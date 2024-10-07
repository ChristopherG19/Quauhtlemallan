package com.app.quauhtlemallan.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.User
import com.app.quauhtlemallan.fragments.ChatBotFragment
import com.app.quauhtlemallan.fragments.GamesFragment
import com.app.quauhtlemallan.fragments.HomeFragment
import com.app.quauhtlemallan.fragments.ProgressFragment
import com.app.quauhtlemallan.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recibir el objeto User serializable desde RegisterActivity o SignInActivity
        val user = intent.getSerializableExtra("user") as? User

        // Configura el BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Si savedInstanceState es nulo, mostramos el fragmento por defecto (HomeFragment)
        if (savedInstanceState == null) {
            val homeFragment = HomeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("user", user) // Pasar el objeto User al fragmento
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit()

            // Seleccionar el ítem de "Inicio" en el BottomNavigationView manualmente
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }

        // Manejar las selecciones del BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_home -> HomeFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("user", user) // Pasar el objeto User a cada fragmento según sea necesario
                    }
                }
                R.id.navigation_chat -> ChatBotFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("user", user)
                    }
                }
                R.id.navigation_settings -> SettingsFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("user", user)
                    }
                }
                R.id.navigation_progress -> ProgressFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("user", user)
                    }
                }
                R.id.navigation_games -> GamesFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("user", user)
                    }
                }
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
            }
            true
        }
    }
}
