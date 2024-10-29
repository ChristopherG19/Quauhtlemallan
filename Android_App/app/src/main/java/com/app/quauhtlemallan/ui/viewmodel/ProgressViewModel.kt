package com.app.quauhtlemallan.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.model.User
import com.app.quauhtlemallan.data.repository.UserRepository
import com.app.quauhtlemallan.util.ProgressState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var userProfile by mutableStateOf<User?>(null)
        private set
    var users by mutableStateOf<List<User>>(emptyList())
        private set
    private val _progressState = MutableStateFlow<ProgressState>(ProgressState.Idle)
    val progressState = _progressState.asStateFlow()
    var userLevel by mutableStateOf(1)
        private set

    init {
        loadUserProfile()
        loadUsersOrderedByScore()
    }

    // Cargar los datos del usuario autenticado
    private fun loadUserProfile() {
        viewModelScope.launch {
            _progressState.value = ProgressState.Loading
            try {
                val userId = userRepository.getCurrentUserId()
                val userProfile = userId?.let { userRepository.getUserProfile(it) }
                if (userProfile != null) {
                    this@ProgressViewModel.userProfile = userProfile
                    userLevel = userRepository.getUserLevel(userProfile.score)
                    _progressState.value = ProgressState.Idle
                } else {
                    _progressState.value = ProgressState.Error("Error al cargar los datos del perfil")
                }
            } catch (e: Exception) {
                _progressState.value = ProgressState.Error("Error al cargar los datos del perfil: ${e.message}")
            }
        }
    }

    // Cargar la lista de usuarios ordenados por puntaje
    private fun loadUsersOrderedByScore() {
        viewModelScope.launch {
            _progressState.value = ProgressState.Loading
            try {
                users = userRepository.getUsersOrderedByScore()
                _progressState.value = ProgressState.Idle
            } catch (e: Exception) {
                _progressState.value = ProgressState.Error("Error al cargar los usuarios: ${e.message}")
            }
        }
    }

    private fun getUserLevel(){}

    fun resetState() {
        _progressState.value = ProgressState.Idle
    }
}