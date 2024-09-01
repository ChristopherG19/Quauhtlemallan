package com.app.quauhtlemallan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.Theme_Quauhtlemallan)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        FacebookSdk.sdkInitialize(this)

        // Analytics event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        //Setup
        setup()
        session()
    }

    override fun onStart(){
        super.onStart()
        val authLayout: LinearLayout = findViewById<LinearLayout>(R.id.authLayout)
        authLayout.visibility = View.VISIBLE
    }

    private fun session(){

        val prefs: SharedPreferences? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email:String? = prefs?.getString("email", null)
        val provider: String? = prefs?.getString("provider", null)

        if(email != null && provider != null){
            val authLayout: LinearLayout = findViewById<LinearLayout>(R.id.authLayout)
            authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun setup() {
        title = "Autenticación"
        val signUpBtn: Button = findViewById<Button>(R.id.signUpButton)
        val loginButton: Button = findViewById<Button>(R.id.loginButton)
        val googleButton: Button = findViewById<Button>(R.id.googleButton)
        val facebookButton: LoginButton = findViewById<(LoginButton)>(R.id.facebook_button)

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

        googleButton.setOnClickListener {
            // Configuración
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        facebookButton.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onCancel() {
                        TODO("Not yet implemented")
                    }

                    override fun onError(error: FacebookException) {
                        TODO("Not yet implemented")
                    }

                    override fun onSuccess(result: LoginResult) {
                        result.let {
                            val token = it.accessToken
                            val credential = FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        showHome(it.result?.user?.email ?: "", ProviderType.GOOGLE)
                                    }
                                }
                        }
                    }

                })

        }

    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                showHome(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch(e: Exception) {
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }
}