package com.app.quauhtlemallan.viewmodels.login

import android.content.Intent
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

class LoginViewModel: ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var showEmptyFieldsAlert by mutableStateOf(false)
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun signInWithEmailAndPassword(
        auth: FirebaseAuth,
        navigateToHome: () -> Unit
    ) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateToHome()
                }
            }
        } else {
            showEmptyFieldsAlert = true
        }
    }

    fun signInWithGoogle(
        result: Intent?,
        auth: FirebaseAuth,
        navigateToHome: () -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { taskG ->
                        if (taskG.isSuccessful) {
                            navigateToHome()
                        } else {
                            _state.update { it.copy(signInError = "Error al iniciar sesión con Google") }
                        }
                    }
            }
        } catch (e: ApiException) {
            _state.update { it.copy(signInError = "Error de Google Sign-In: ${e.message}") }
        }
    }

    fun resetAlert() {
        showEmptyFieldsAlert = false
    }

}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isLoading: Boolean = false
)

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)