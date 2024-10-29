package com.app.quauhtlemallan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userLevel = MutableStateFlow(1)
    val userLevel: StateFlow<Int> = _userLevel

    private val _pointsToNextLevel = MutableStateFlow(0)
    val pointsToNextLevel: StateFlow<Int> = _pointsToNextLevel

    private val _currentPointsWithinLevel = MutableStateFlow(0)
    val currentPointsWithinLevel: StateFlow<Int> = _currentPointsWithinLevel

    init {
        viewModelScope.launch {
            val totalScore = userRepository.getUserTotalScore()
            val (level, pointsToNext, pointsWithin) = userRepository.calculateUserLevel(totalScore)

            _userLevel.value = level
            _pointsToNextLevel.value = pointsToNext
            _currentPointsWithinLevel.value = pointsWithin
        }
    }
}
