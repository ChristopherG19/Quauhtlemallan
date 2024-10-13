package com.app.quauhtlemallan.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.repository.UserRepository
import com.app.quauhtlemallan.util.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var selectedCountry by mutableStateOf("Guatemala")
        private set
    var imageUrl by mutableStateOf("")
        private set

    var isPasswordEnabled by mutableStateOf(true)

    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.Idle)
    val settingsStateFlow = _settingsState.asStateFlow()

    init {
        loadUserData()
    }

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onCountryChange(newCountry: String) {
        selectedCountry = newCountry
    }

    fun loadUserData() {
        viewModelScope.launch {
            _settingsState.value = SettingsState.Loading
            try {
                val userId = userRepository.getCurrentUserId()

                if (userId != null) {
                    val userProfile = userRepository.getUserProfile(userId)
                    userProfile?.let {
                        username = it.username
                        email = it.email
                        selectedCountry = it.country
                        imageUrl = it.profileImage
                        isPasswordEnabled = userRepository.isEmailProvider()
                        _settingsState.value = SettingsState.Idle
                    } ?: run {
                        _settingsState.value = SettingsState.Error("No se pudieron cargar los datos del usuario.")
                    }
                } else {
                    _settingsState.value = SettingsState.Error("Usuario no autenticado.")
                }
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al cargar los datos del usuario: ${e.message}")
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        val userId = userRepository.getCurrentUserId()

        if (userId != null) {
            viewModelScope.launch {
                val newImageUrl = userRepository.uploadProfileImage(imageUri)
                if (newImageUrl != null) {
                    imageUrl = imageUri.toString()
                    userRepository.updateUserProfile(username, selectedCountry, imageUrl)
                    _settingsState.value = SettingsState.Success
                } else {
                    _settingsState.value = SettingsState.Error("Error al subir la imagen.")
                }
            }
        } else {
            _settingsState.value = SettingsState.Error("No estás autenticado.")
        }
    }

    fun updateProfile() {
        viewModelScope.launch {
            _settingsState.value = SettingsState.Loading
            try {
                if (imageUrl.isNotEmpty()) {
                    val newImageUrl = userRepository.uploadProfileImage(Uri.parse(imageUrl))
                    Log.e("returntoUploadP", newImageUrl.toString())
                    if (newImageUrl != null) {
                        imageUrl = newImageUrl // Actualiza el imageUrl con el nuevo valor
                    } else {
                        _settingsState.value = SettingsState.Error("Error al subir la imagen.")
                        return@launch
                    }
                }

                val success = userRepository.updateUserProfile(
                    username = username,
                    country = selectedCountry,
                    profileImage = imageUrl
                )
                if (password.isNotEmpty() && isPasswordEnabled) {
                    userRepository.updatePassword(password)
                }
                if (success) {
                    _settingsState.value = SettingsState.Success
                } else {
                    _settingsState.value = SettingsState.Error("Error al actualizar el perfil")
                }
            } catch (e: Exception) {
                _settingsState.value = SettingsState.Error("Error al actualizar el perfil: ${e.message}")
            }
        }
    }

    fun resetState() {
        _settingsState.value = SettingsState.Idle
    }
}