package com.app.quauhtlemallan.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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

    private val _discoveryPercentage = mutableStateOf(0)
    val discoveryPercentage: MutableState<Int> = _discoveryPercentage

    init {
        loadAllBadgesAndDiscovery()
    }

    private fun loadAllBadgesAndDiscovery() {
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

    suspend fun getBadgeProgress(badgeId: String): Int {
        return userRepository.getUserBadgeProgress(badgeId)
    }

    fun getBadgesByCategory(categoryId: String): StateFlow<AchievementsState> {
        return badgesCache[categoryId]?.let { cachedBadges ->
            MutableStateFlow(AchievementsState.Success(cachedBadges))
        } ?: run {
            val categoryStateFlow = MutableStateFlow<AchievementsState>(AchievementsState.Loading)
            viewModelScope.launch {
                try {
                    val badges = userRepository.getBadgesByCategory(categoryId)
                    badgesCache[categoryId] = badges
                    categoryStateFlow.value = AchievementsState.Success(badges)
                } catch (e: Exception) {
                    categoryStateFlow.value = AchievementsState.Error("Error al cargar las insignias de la categoría: ${e.message}")
                }
            }
            categoryStateFlow
        }
    }
}
