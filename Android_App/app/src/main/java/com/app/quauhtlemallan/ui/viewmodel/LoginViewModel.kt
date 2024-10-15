package com.app.quauhtlemallan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.util.LoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var showEmptyFieldsAlert by mutableStateOf(false)
        private set

    var _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginStateFlow = _loginState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun signInWithEmailAndPassword(onNavigate: (User) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Por favor, completa todos los campos.")
            return
        }

        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val userProfile = userRepository.getUserProfile(firebaseUser.uid)
                    userProfile?.let {
                        _loginState.value = LoginState.Success(it)
                        onNavigate(it)
                    } ?: run {
                        _loginState.value = LoginState.Error("No se pudo obtener el perfil del usuario.")
                    }
                } else {
                    _loginState.value = LoginState.Error("No se pudo obtener el usuario.")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _loginState.value = LoginState.Error("Credenciales incorrectas.")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error al iniciar sesión.")
            }
        }
    }

    fun signInWithGoogle(idToken: String, onNavigate: (User) -> Unit) {
        if (idToken.isBlank()) {
            _loginState.value = LoginState.Error("El token de Google es inválido.")
            return
        }

        _loginState.value = LoginState.Loading
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            try {
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val userProfile = userRepository.getUserProfile(firebaseUser.uid)
                    if (userProfile != null) {
                        _loginState.value = LoginState.Success(userProfile)
                        onNavigate(userProfile)
                    } else {
                        val newUser = User(
                            id = firebaseUser.uid,
                            username = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                            profileImage = firebaseUser.photoUrl?.toString() ?: "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e",
                        )

                        val success = userRepository.createUserProfile(newUser)
                        if (success) {
                            _loginState.value = LoginState.Success(newUser)
                        } else {
                            _loginState.value = LoginState.Error("Error al crear perfil de usuario.")
                        }
                    }
                } else {
                    _loginState.value = LoginState.Error("Error al iniciar sesión con Google.")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _loginState.value = LoginState.Error("Credenciales de Google inválidas.")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de Google Sign-In.")
            }
        }
    }

    fun resetAlert() {
        _loginState.value = LoginState.Idle
    }
}
