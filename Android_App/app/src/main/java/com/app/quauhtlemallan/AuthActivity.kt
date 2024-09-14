package com.app.quauhtlemallan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(2000)
        setTheme(R.style.Theme_Quauhtlemallan)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Analytics event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        //Setup
        setup()
    }

    private fun setup() {
        title = "Autenticación"
        val signUpBtn: Button = findViewById<Button>(R.id.signUpButton)
        val loginButton: Button = findViewById<Button>(R.id.loginButton)

        signUpBtn.setOnClickListener{
            val editText: EditText = findViewById(R.id.emailEditText)
            val pwdText: EditText = findViewById(R.id.passwordEditText)
            if (editText.text.isNotEmpty() && pwdText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(editText.text.toString(),
                        pwdText.text.toString()).addOnCompleteListener{
                        if (it.isSuccessful){
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        loginButton.setOnClickListener{
            val editText: EditText = findViewById(R.id.emailEditText)
            val pwdText: EditText = findViewById(R.id.passwordEditText)
            if (editText.text.isNotEmpty() && pwdText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(editText.text.toString(),
                        pwdText.text.toString()).addOnCompleteListener{
                        if (it.isSuccessful){
                            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            Toast.makeText(this, "Login fallido", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}