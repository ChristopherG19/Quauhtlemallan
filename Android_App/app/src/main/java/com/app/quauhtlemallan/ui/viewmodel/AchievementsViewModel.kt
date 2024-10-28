package com.app.quauhtlemallan.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quauhtlemallan.data.model.AchievementData
import com.app.quauhtlemallan.data.repository.UserRepository
import com.app.quauhtlemallan.util.AchievementsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AchievementsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val badgesCache = mutableMapOf<String, List<AchievementData>>()

    private val _badgesState = MutableStateFlow<AchievementsState>(AchievementsState.Loading)
    val badgesState: StateFlow<AchievementsState> = _badgesState

    private val _discoveryPercentage = mutableStateOf(0)
    val discoveryPercentage: MutableState<Int> = _discoveryPercentage

    init {
        loadDiscoveryPercentage()
        loadAllBadges()
    }

    suspend fun getBadgeProgress(badgeId: String): Int {
        return userRepository.getUserBadgeProgress(badgeId)
    }

    fun refreshDiscoveryPercentage() {
        loadDiscoveryPercentage()
    }

    private fun loadDiscoveryPercentage() {
        viewModelScope.launch {
            _badgesState.value = AchievementsState.Loading
            try {
                val badges = userRepository.getAllBadges()
                val totalProgress = badges.sumOf { badge ->
                    userRepository.getUserBadgeProgress(badge.id)
                }
                val maxPoints = badges.sumOf { it.maxPoints }

                val percentage = if (maxPoints > 0) {
                    (totalProgress * 100) / maxPoints
                } else {
                    0
                }

                _discoveryPercentage.value = percentage

                _badgesState.value = AchievementsState.Success(badges)
            } catch (e: Exception) {
                _badgesState.value = AchievementsState.Error("Error al cargar las insignias: ${e.message}")
            }
        }
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