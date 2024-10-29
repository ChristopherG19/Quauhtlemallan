package com.app.quauhtlemallan.util

import com.app.quauhtlemallan.data.model.AchievementData
import com.app.quauhtlemallan.data.model.User

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

data class SignInResult(
    val data: User?,
    val errorMessage: String?
)

sealed class SettingsState {
    object Idle : SettingsState()
    object Loading : SettingsState()
    object Success : SettingsState()
    data class Error(val message: String) : SettingsState()
}

sealed class ProgressState {
    object Idle : ProgressState()
    object Loading : ProgressState()
    data class Error(val message: String) : ProgressState()
}

sealed class AchievementsState {
    object Loading : AchievementsState()
    data class Success(val badges: List<AchievementData>) : AchievementsState()
    data class Error(val message: String) : AchievementsState()
}
