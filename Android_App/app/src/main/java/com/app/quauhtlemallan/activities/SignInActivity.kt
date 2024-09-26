package com.app.quauhtlemallan.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.quauhtlemallan.HomeActivity
import com.app.quauhtlemallan.ProviderType
import com.app.quauhtlemallan.R
import com.app.quauhtlemallan.data.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.Theme_Quauhtlemallan)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.title = "Iniciar Sesión"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        FacebookSdk.sdkInitialize(applicationContext)

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        if (isLoggedIn) {
            LoginManager.getInstance().logOut()
        }

        // Inicializar el CallbackManager
        callbackManager = CallbackManager.Factory.create()

        // Referencias a los elementos de la UI
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val signInButton: Button = findViewById(R.id.signInButton)
        val googleSignInButton: Button = findViewById(R.id.googleSignInButton)
        val facebookButton: LoginButton = findViewById(R.id.facebook_button)

        val auth = FirebaseAuth.getInstance()

        // Evento de clic para iniciar sesión con email y contraseña
        signInButton.setOnClickListener {
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            val userId = user?.uid ?: return@addOnCompleteListener

                            // Obtener la referencia a la base de datos de Firebase
                            val usersRef = FirebaseDatabase.getInstance().getReference("users")

                            // Cargar la información del usuario desde Firebase antes de proceder
                            usersRef.child(userId).get().addOnSuccessListener { snapshot ->
                                val country = snapshot.child("country").getValue(String::class.java) ?: "País no configurado"

                                // Ahora que los datos están cargados, navega a la HomeActivity
                                showHome(user.email ?: "", ProviderType.BASIC, country)
                            }.addOnFailureListener {
                                showAlert("Error", "No se pudo obtener la información del usuario.")
                            }
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
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        // Configurar el LoginButton de Facebook
        facebookButton.setPermissions("email")
        facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                showAlert("Autenticación", "Inicio de sesión cancelado.")
            }

            override fun onError(error: FacebookException) {
                error.message?.let { showAlert("Autenticación", it) }
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, mostrar la pantalla principal
                    val user = FirebaseAuth.getInstance().currentUser
                    createUserProfile(user, ProviderType.FACEBOOK)
                    val userId = user?.uid ?: return@addOnCompleteListener

                    // Obtener la referencia a la base de datos de Firebase
                    val usersRef = FirebaseDatabase.getInstance().getReference("users")

                    // Cargar la información del usuario desde Firebase antes de proceder
                    usersRef.child(userId).get().addOnSuccessListener { snapshot ->
                        val country = snapshot.child("country").getValue(String::class.java)
                            ?: "País no configurado"

                        // Ahora que los datos están cargados, navega a la HomeActivity
                        showHome(user.email ?: "", ProviderType.FACEBOOK, country)
                    }
                } else {
                    // Manejar errores
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        // Error de colisión de cuentas
                        val exception = task.exception as FirebaseAuthUserCollisionException
                        val email = exception.email
                        // Llamar a la función para resolver la colisión
                        resolveAccountCollision(email, credential)
                    } else {
                        // Otros errores
                        val errorMessage = task.exception?.localizedMessage ?: "Error desconocido."
                        showAlert("Error de Autenticación", errorMessage)
                    }
                }
            }
    }

    private fun resolveAccountCollision(email: String?, credential: AuthCredential) {
        if (email == null) {
            showAlert("Error", "No se pudo obtener el correo electrónico de la cuenta existente.")
            return
        }

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods ?: emptyList()
                    if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                        // El usuario está registrado con email y contraseña
                        showAlert("Cuenta Existente", "Por favor, inicia sesión con tu correo y contraseña para vincular tu cuenta de Facebook.")
                    } else {
                        // Otros proveedores
                        LoginManager.getInstance().logOut()
                        showAlert("Cuenta Existente con este correo", "Por favor, inicia sesión con Google")
                    }
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Error desconocido al verificar los métodos de inicio de sesión."
                    showAlert("Error", errorMessage)
                }
            }
    }



    private fun showHome(email: String, provider: ProviderType, country: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            putExtra("country", country)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Importante: primero pasa los resultados al CallbackManager de Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data)
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
                                createUserProfile(it.result?.user, ProviderType.GOOGLE)
                                // Inicio de sesión exitoso, mostrar la pantalla principal
                                val user = FirebaseAuth.getInstance().currentUser
                                val userId = user?.uid ?: return@addOnCompleteListener

                                // Obtener la referencia a la base de datos de Firebase
                                val usersRef = FirebaseDatabase.getInstance().getReference("users")

                                // Cargar la información del usuario desde Firebase antes de proceder
                                usersRef.child(userId).get().addOnSuccessListener { snapshot ->
                                    val country = snapshot.child("country").getValue(String::class.java)
                                        ?: "País no configurado"

                                    // Ahora que los datos están cargados, navega a la HomeActivity
                                    showHome(account.email ?: "", ProviderType.GOOGLE, country)
                                }
                            } else {
                                showAlert("Error de Autenticación", "No se pudo autenticar con Google.")
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert("Error de Google Sign-In", e.message ?: "Error desconocido.")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val usernameEditText: EditText = findViewById(R.id.usernameEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)

        usernameEditText.text.clear()
        passwordEditText.text.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Cierra la actividad actual y regresa a la anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createUserProfile(user: FirebaseUser?, provider: ProviderType) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val userId = user?.uid ?: return

        // Verifica si el usuario ya tiene un valor para "country" en Firebase antes de escribirlo
        usersRef.child(userId).get().addOnSuccessListener { snapshot ->
            var country = snapshot.child("country").getValue(String::class.java) ?: ""

            if (country.isEmpty()) {
                country = "País no seleccionado"
            }

            val userProfile = User(
                username = user.displayName ?: "",
                email = user.email ?: "",
                country = country
            )

            usersRef.child(userId).setValue(userProfile)
        }
    }

}
