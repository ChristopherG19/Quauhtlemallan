package com.app.quauhtlemallan.viewmodels.authentication

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.app.quauhtlemallan.viewmodels.data.*
import com.google.firebase.database.FirebaseDatabase

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var showEmptyFieldsAlert by mutableStateOf(false)
        private set

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun signInWithEmailAndPassword(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        navigateToHome: (User) -> Unit
    ) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        Util.loadUserProfile(firebaseUser, ProviderType.BASIC, database) { userProfile ->
                            navigateToHome(userProfile)
                        }
                    }
                } else {
                    _state.update { it.copy(signInError = "Error al iniciar sesión con email y contraseña.") }
                }
            }
        } else {
            showEmptyFieldsAlert = true
        }
    }

    fun signInWithGoogle(
        result: Intent?,
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        navigateToHome: (User) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { taskG ->
                    if (taskG.isSuccessful) {
                        val firebaseUser = taskG.result?.user
                        if (firebaseUser != null) {
                            Log.d("LoginViewModel", "Google User email: ${firebaseUser.email}")
                            Util.loadUserProfile(firebaseUser, ProviderType.GOOGLE, database) { userProfile ->
                                navigateToHome(userProfile)
                            }
                        }
                    } else {
                        _state.update { it.copy(signInError = "Error de Autenticación: No se pudo autenticar con Google.") }
                    }
                }
            }
        } catch (e: ApiException) {
            _state.update { it.copy(signInError = "Error de Google Sign-In: ${e.message ?: "Error desconocido."}") }
        }
    }

    fun resetAlert() {
        _state.update { it.copy(signInError = null) }
        showEmptyFieldsAlert = false
    }
}
