/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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