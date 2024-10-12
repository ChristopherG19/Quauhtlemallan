package com.app.quauhtlemallan.viewmodels.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.quauhtlemallan.viewmodels.data.ProviderType
import com.app.quauhtlemallan.viewmodels.data.SignInState
import com.app.quauhtlemallan.viewmodels.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var selectedCountry by mutableStateOf("Guatemala")
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

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
    }

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onCountryChange(newCountry: String) {
        selectedCountry = newCountry
    }

    fun registerUser(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        navigateToHome: (User) -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || username.isBlank() || confirmPassword.isBlank()) {
            _state.update { it.copy(signInError = "Por favor, completa todos los campos.") }
            showEmptyFieldsAlert = true
        } else if (password != confirmPassword) {
            _state.update { it.copy(signInError = "Las contraseñas no coinciden.") }
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        Util.createUserProfile(firebaseUser, ProviderType.BASIC, database, isRegistration = true, username, selectedCountry) { userProfile ->
                            navigateToHome(userProfile)
                        }
                    }
                } else {
                    _state.update { it.copy(signInError = "Error al registrar usuario: ${task.exception?.message}") }
                }
            }
        }
    }

    fun resetAlert() {
        _state.update { it.copy(signInError = null) }
        showEmptyFieldsAlert = false
    }
}
