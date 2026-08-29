package com.swahilib.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.games.EngagementRepo
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.data.worker.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val engageRepo: EngagementRepo,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _lastLoginOutcome = MutableStateFlow<EngagementRepo.DailyLoginOutcome?>(null)
    val lastLoginOutcome: StateFlow<EngagementRepo.DailyLoginOutcome?> = _lastLoginOutcome.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            if (prefsRepo.installDate == 0L) {
                prefsRepo.installDate = System.currentTimeMillis()
            }
            Log.d(TAG, "Scheduling sync on launch")
            SyncScheduler.scheduleOnLaunch(context)

            runCatching { engageRepo.onAppOpen() }
                .onSuccess { _lastLoginOutcome.value = it }
                .onFailure { Log.e(TAG, "onAppOpen failed: ${it.message}", it) }

            _isReady.value = true
        }
    }

    fun consumeLoginOutcome() { _lastLoginOutcome.value = null }

    companion object {
        private const val TAG = "MainViewModel"
    }
}