package com.app.quauhtlemallan

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private val FACEBOOK = 64206 // Código de respuesta para Facebook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.title = "Iniciar Sesión"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Referencias a los elementos de la UI
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val signInButton: Button = findViewById(R.id.signInButton)
        val googleSignInButton: Button = findViewById(R.id.googleSignInButton)
        val facebookSignInButton: Button = findViewById(R.id.facebookSignInButton)

        val auth = FirebaseAuth.getInstance()

        // Evento de clic para iniciar sesión con email y contraseña
        signInButton.setOnClickListener {
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        } else {
                            showAlert("Error de Autenticación", "Usuario o contraseña incorrectos.")
                        }
                    }
            } else {
                showAlert("Campos Vacíos", "Por favor, completa todos los campos.")
            }
        }

        // Evento de clic para iniciar sesión con Google
        googleSignInButton.setOnClickListener {
            // Configuración de Google Sign-In
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        // Evento de clic para iniciar sesión con Facebook
        facebookSignInButton.setOnClickListener {
            // Configuración de Facebook Login
            val callbackManager = CallbackManager.Factory.create()
            val loginManager = LoginManager.getInstance()

            loginManager.logInWithReadPermissions(this, listOf("email"))
            loginManager.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        val token = result.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    showHome(it.result?.user?.email ?: "", ProviderType.FACEBOOK)
                                } else {
                                    showAlert("Error de Autenticación", "No se pudo autenticar con Facebook.")
                                }
                            }
                    }

                    override fun onCancel() { /* Manejar cancelación si es necesario */ }

                    override fun onError(error: FacebookException) {
                        showAlert("Error de Facebook", error.message ?: "Error desconocido.")
                    }
                })
        }
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // Manejar resultados de actividades
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                showAlert("Error de Autenticación", "No se pudo autenticar con Google.")
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert("Error de Google Sign-In", e.message ?: "Error desconocido.")
            }
        }
        // Añade manejo para Facebook si es necesario
    }

    override fun onResume() {
        super.onResume()
        // Referencias a los elementos de la UI
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)

        usernameEditText.text.clear()
        passwordEditText.text.clear()
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
