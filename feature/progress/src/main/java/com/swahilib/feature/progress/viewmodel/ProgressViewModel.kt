package com.swahilib.feature.progress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.ActivityRecommendation
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.Challenge
import com.swahilib.core.engagement.model.StatisticsSummary
import com.swahilib.core.engagement.model.UserProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val engageRepo: EngagementRepo,
) : ViewModel() {

    private val _progress = MutableStateFlow<UserProgress?>(null)
    val progress: StateFlow<UserProgress?> = _progress.asStateFlow()

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges.asStateFlow()

    private val _stats = MutableStateFlow<StatisticsSummary?>(null)
    val stats: StateFlow<StatisticsSummary?> = _stats.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _recommendations = MutableStateFlow<List<ActivityRecommendation>>(emptyList())
    val recommendations: StateFlow<List<ActivityRecommendation>> = _recommendations.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _progress.value = engageRepo.currentProgress()
            _challenges.value = engageRepo.activeChallenges()
            _stats.value = engageRepo.statistics()
            _achievements.value = engageRepo.achievementsWithStatus()
            _recommendations.value = engageRepo.recommendedActivities()
        }
    }

    fun completeActivity(challengeId: String, activityId: String, secondsSpent: Int = 0) {
        viewModelScope.launch {
            engageRepo.markActivityComplete(challengeId, activityId, secondsSpent)
            refresh()
        }
    }
}
