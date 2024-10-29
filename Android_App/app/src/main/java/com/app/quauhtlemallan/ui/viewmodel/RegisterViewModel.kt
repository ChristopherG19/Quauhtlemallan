package com.app.quauhtlemallan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.util.RegisterState
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

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

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerStateFlow = _registerState.asStateFlow()

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

    fun registerUser(onNavigate: (User) -> Unit) {
        if (email.isBlank() || password.isBlank() || username.isBlank() || confirmPassword.isBlank()) {
            _registerState.value = RegisterState.Error("Por favor, completa todos los campos.")
        } else if (password != confirmPassword) {
            _registerState.value = RegisterState.Error("Las contraseñas no coinciden.")
        } else {
            viewModelScope.launch {
                try {
                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                    val firebaseUser = result.user
                    val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e"
                    if (firebaseUser != null) {
                        val newUser = User(
                            id = firebaseUser.uid,
                            username = username,
                            email = email,
                            country = selectedCountry,
                            profileImage = firebaseUser.photoUrl?.toString() ?: defaultImageUrl
                        )
                        val success = userRepository.createUserProfile(newUser)
                        if (success) {
                            onNavigate(newUser)
                        }
                    }
                } catch (e: Exception) {
                    _registerState.value = RegisterState.Error("Error al registrar usuario.")
                }
            }
        }
    }

    fun resetAlert() {
        _registerState.value = RegisterState.Idle
    }
}
