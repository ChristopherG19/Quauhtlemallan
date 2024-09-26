package com.app.quauhtlemallan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.app.quauhtlemallan.activities.RegisterActivity
import com.app.quauhtlemallan.activities.SignInActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000) // Si deseas mantener el delay inicial
        setTheme(R.style.Theme_Quauhtlemallan)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Referencias a los botones
        val signInButton: Button = findViewById(R.id.signInButton)
        val registerButton: Button = findViewById(R.id.registerButton)

        // Eventos de clic para navegar a SignInActivity
        signInButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Eventos de clic para navegar a RegisterActivity
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
