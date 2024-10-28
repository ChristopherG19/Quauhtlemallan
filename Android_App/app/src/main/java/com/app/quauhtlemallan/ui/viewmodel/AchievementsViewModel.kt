package com.app.quauhtlemallan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.model.AchievementData
import com.app.quauhtlemallan.data.repository.UserRepository
import com.app.quauhtlemallan.util.AchievementsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AchievementsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val badgesCache = mutableMapOf<String, List<AchievementData>>()

    private val _badgesState = MutableStateFlow<AchievementsState>(AchievementsState.Loading)
    val badgesState: StateFlow<AchievementsState> = _badgesState

    init {
        loadAllBadges()
    }

    private fun loadAllBadges() {
        viewModelScope.launch {
            _badgesState.value = AchievementsState.Loading
            try {
                val badges = userRepository.getAllBadges()
                _badgesState.value = AchievementsState.Success(badges)
            } catch (e: Exception) {
                _badgesState.value = AchievementsState.Error("Error al cargar las insignias: ${e.message}")
            }
        }
    }

    fun getBadgesByCategory(categoryId: String): StateFlow<AchievementsState> {
        if (badgesCache.containsKey(categoryId)) {
            _badgesState.value = AchievementsState.Success(badgesCache[categoryId]!!)
        } else {
            viewModelScope.launch {
                _badgesState.value = AchievementsState.Loading
                try {
                    val badges = userRepository.getBadgesByCategory(categoryId)
                    badgesCache[categoryId] = badges
                    _badgesState.value = AchievementsState.Success(badges)
                } catch (e: Exception) {
                    _badgesState.value = AchievementsState.Error("Error al cargar las insignias: ${e.message}")
                }
            }
        }
        return _badgesState
    }
}