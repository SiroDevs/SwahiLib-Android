package com.swahilib.feature.settings

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

    // ── Neno la Siku ──────────────────────────────────────────────────────
    private val _nenoEnabled = MutableStateFlow(prefsRepo.nenoNotifEnabled)
    val nenoEnabled: StateFlow<Boolean> = _nenoEnabled.asStateFlow()

    private val _nenoHour = MutableStateFlow(prefsRepo.nenoNotifHour)
    val nenoHour: StateFlow<Int> = _nenoHour.asStateFlow()

    private val _nenoMinute = MutableStateFlow(prefsRepo.nenoNotifMinute)
    val nenoMinute: StateFlow<Int> = _nenoMinute.asStateFlow()

    // ── Methali ya Siku ───────────────────────────────────────────────────
    private val _methaliEnabled = MutableStateFlow(prefsRepo.methaliNotifEnabled)
    val methaliEnabled: StateFlow<Boolean> = _methaliEnabled.asStateFlow()

    private val _methaliHour = MutableStateFlow(prefsRepo.methaliNotifHour)
    val methaliHour: StateFlow<Int> = _methaliHour.asStateFlow()

    private val _methaliMinute = MutableStateFlow(prefsRepo.methaliNotifMinute)
    val methaliMinute: StateFlow<Int> = _methaliMinute.asStateFlow()

    fun setNenoEnabled(enabled: Boolean) {
        _nenoEnabled.value = enabled
        prefsRepo.nenoNotifEnabled = enabled
        NotificationScheduler.scheduleNenoLaSiku(context, enabled, _nenoHour.value, _nenoMinute.value)
    }

    fun setNenoTime(hour: Int, minute: Int) {
        _nenoHour.value = hour
        _nenoMinute.value = minute
        prefsRepo.nenoNotifHour = hour
        prefsRepo.nenoNotifMinute = minute
        if (_nenoEnabled.value) {
            NotificationScheduler.scheduleNenoLaSiku(context, true, hour, minute)
        }
    }

    fun setMethaliEnabled(enabled: Boolean) {
        _methaliEnabled.value = enabled
        prefsRepo.methaliNotifEnabled = enabled
        NotificationScheduler.scheduleMethaliYaSiku(context, enabled, _methaliHour.value, _methaliMinute.value)
    }

    fun setMethaliTime(hour: Int, minute: Int) {
        _methaliHour.value = hour
        _methaliMinute.value = minute
        prefsRepo.methaliNotifHour = hour
        prefsRepo.methaliNotifMinute = minute
        if (_methaliEnabled.value) {
            NotificationScheduler.scheduleMethaliYaSiku(context, true, hour, minute)
        }
    }

    fun clearData() {
        viewModelScope.launch {
            prefsRepo.isDataLoaded = false
        }
    }
}
