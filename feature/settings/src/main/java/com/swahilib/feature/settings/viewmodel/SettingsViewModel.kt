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
    private val _nenoEnabled = MutableStateFlow(prefsRepo.wordNotifEnabled)
    val nenoEnabled: StateFlow<Boolean> = _nenoEnabled.asStateFlow()

    private val _nenoHour = MutableStateFlow(prefsRepo.wordNotifHour)
    val nenoHour: StateFlow<Int> = _nenoHour.asStateFlow()

    private val _nenoMinute = MutableStateFlow(prefsRepo.wordNotifMinute)
    val nenoMinute: StateFlow<Int> = _nenoMinute.asStateFlow()

    private val _methaliEnabled = MutableStateFlow(prefsRepo.proverbNotifEnabled)
    val methaliEnabled: StateFlow<Boolean> = _methaliEnabled.asStateFlow()

    private val _methaliHour = MutableStateFlow(prefsRepo.proverbNotifHour)
    val methaliHour: StateFlow<Int> = _methaliHour.asStateFlow()

    private val _methaliMinute = MutableStateFlow(prefsRepo.proverbNotifMinute)
    val methaliMinute: StateFlow<Int> = _methaliMinute.asStateFlow()

    fun setNenoEnabled(enabled: Boolean) {
        _nenoEnabled.value = enabled
        prefsRepo.wordNotifEnabled = enabled
        NotificationScheduler.scheduleDailyWord(context, enabled, _nenoHour.value, _nenoMinute.value)
    }

    fun setNenoTime(hour: Int, minute: Int) {
        _nenoHour.value = hour
        _nenoMinute.value = minute
        prefsRepo.wordNotifHour = hour
        prefsRepo.wordNotifMinute = minute
        if (_nenoEnabled.value) {
            NotificationScheduler.scheduleDailyWord(context, true, hour, minute)
        }
    }

    fun setMethaliEnabled(enabled: Boolean) {
        _methaliEnabled.value = enabled
        prefsRepo.proverbNotifEnabled = enabled
        NotificationScheduler.scheduleDailyProverb(context, enabled, _methaliHour.value, _methaliMinute.value)
    }

    fun setMethaliTime(hour: Int, minute: Int) {
        _methaliHour.value = hour
        _methaliMinute.value = minute
        prefsRepo.proverbNotifHour = hour
        prefsRepo.proverbNotifMinute = minute
        if (_methaliEnabled.value) {
            NotificationScheduler.scheduleDailyProverb(context, true, hour, minute)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            prefsRepo.isDataLoaded = false
        }
    }
}