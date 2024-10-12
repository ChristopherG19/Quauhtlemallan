package com.app.quauhtlemallan.viewmodels.data

import java.io.Serializable

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
    val data: User?,
    val errorMessage: String?
)

data class User(
    val username: String = "",
    val email: String = "",
    val country: String = "Guatemala",
    val profileImage: String = "https://firebasestorage.googleapis.com/v0/b/quauhtlemallan-d86d0.appspot.com/o/ic_default.png?alt=media&token=4edc3e81-ecb0-4a88-8d46-8cf2c2dfc69e",
    val score: Int = 0,
    val achievements: Int = 0
) : Serializable