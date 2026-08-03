package com.swahilib.feature.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.notifications.NotificationScheduler
import com.swahilib.core.data.repos.PrefsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _wordEnabled = MutableStateFlow(prefsRepo.wordNotifEnabled)
    val wordEnabled: StateFlow<Boolean> = _wordEnabled.asStateFlow()

    private val _wordHour = MutableStateFlow(prefsRepo.wordNotifHour)
    val wordHour: StateFlow<Int> = _wordHour.asStateFlow()

    private val _wordMinute = MutableStateFlow(prefsRepo.wordNotifMinute)
    val wordMinute: StateFlow<Int> = _wordMinute.asStateFlow()

    private val _proverbEnabled = MutableStateFlow(prefsRepo.proverbNotifEnabled)
    val proverbEnabled: StateFlow<Boolean> = _proverbEnabled.asStateFlow()

    private val _proverbHour = MutableStateFlow(prefsRepo.proverbNotifHour)
    val proverbHour: StateFlow<Int> = _proverbHour.asStateFlow()

    private val _proverbMinute = MutableStateFlow(prefsRepo.proverbNotifMinute)
    val proverbMinute: StateFlow<Int> = _proverbMinute.asStateFlow()

    private val _challengeEnabled = MutableStateFlow(prefsRepo.challengeNotifEnabled)
    val challengeEnabled: StateFlow<Boolean> = _challengeEnabled.asStateFlow()

    private val _challengeHour = MutableStateFlow(prefsRepo.challengeNotifHour)
    val challengeHour: StateFlow<Int> = _challengeHour.asStateFlow()

    private val _challengeMinute = MutableStateFlow(prefsRepo.challengeNotifMinute)
    val challengeMinute: StateFlow<Int> = _challengeMinute.asStateFlow()

    private val _summaryEnabled = MutableStateFlow(prefsRepo.weeklySummaryNotifEnabled)
    val summaryEnabled: StateFlow<Boolean> = _summaryEnabled.asStateFlow()

    private val _summaryHour = MutableStateFlow(prefsRepo.weeklySummaryNotifHour)
    val summaryHour: StateFlow<Int> = _summaryHour.asStateFlow()

    private val _summaryMinute = MutableStateFlow(prefsRepo.weeklySummaryNotifMinute)
    val summaryMinute: StateFlow<Int> = _summaryMinute.asStateFlow()

    fun setNenoEnabled(enabled: Boolean) {
        _wordEnabled.value = enabled
        prefsRepo.wordNotifEnabled = enabled
        NotificationScheduler.scheduleDailyWord(context, enabled, _wordHour.value, _wordMinute.value)
    }

    fun setNenoTime(hour: Int, minute: Int) {
        _wordHour.value = hour
        _wordMinute.value = minute
        prefsRepo.wordNotifHour = hour
        prefsRepo.wordNotifMinute = minute
        if (_wordEnabled.value) {
            NotificationScheduler.scheduleDailyWord(context, true, hour, minute)
        }
    }

    fun setMethaliEnabled(enabled: Boolean) {
        _proverbEnabled.value = enabled
        prefsRepo.proverbNotifEnabled = enabled
        NotificationScheduler.scheduleDailyProverb(context, enabled, _proverbHour.value, _proverbMinute.value)
    }

    fun setMethaliTime(hour: Int, minute: Int) {
        _proverbHour.value = hour
        _proverbMinute.value = minute
        prefsRepo.proverbNotifHour = hour
        prefsRepo.proverbNotifMinute = minute
        if (_proverbEnabled.value) {
            NotificationScheduler.scheduleDailyProverb(context, true, hour, minute)
        }
    }

    fun setChallengeEnabled(enabled: Boolean) {
        _challengeEnabled.value = enabled
        prefsRepo.challengeNotifEnabled = enabled
        NotificationScheduler.scheduleDailyChallenge(
            context, enabled, _challengeHour.value, _challengeMinute.value
        )
    }

    fun setChallengeTime(hour: Int, minute: Int) {
        _challengeHour.value = hour
        _challengeMinute.value = minute
        prefsRepo.challengeNotifHour = hour
        prefsRepo.challengeNotifMinute = minute
        if (_challengeEnabled.value) {
            NotificationScheduler.scheduleDailyChallenge(context, true, hour, minute)
        }
    }

    fun setSummaryEnabled(enabled: Boolean) {
        _summaryEnabled.value = enabled
        prefsRepo.weeklySummaryNotifEnabled = enabled
        NotificationScheduler.scheduleWeeklySummary(
            context, enabled, _summaryHour.value, _summaryMinute.value
        )
    }

    fun setSummaryTime(hour: Int, minute: Int) {
        _summaryHour.value = hour
        _summaryMinute.value = minute
        prefsRepo.weeklySummaryNotifHour = hour
        prefsRepo.weeklySummaryNotifMinute = minute
        if (_summaryEnabled.value) {
            NotificationScheduler.scheduleWeeklySummary(context, true, hour, minute)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            prefsRepo.isDataLoaded = false
        }
    }
}