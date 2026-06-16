package com.swahilib

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.PrefsRepo
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
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            if (prefsRepo.installDate == 0L) {
                prefsRepo.installDate = System.currentTimeMillis()
            }

            when {
                !prefsRepo.isDataLoaded -> {
                    Log.d(TAG, "First install – scheduling install sync via WorkManager")
                    SyncScheduler.scheduleInstallSync(context)
                }
                prefsRepo.needsDailySync() -> {
                    Log.d(TAG, "Daily sync due – scheduling background sync via WorkManager")
                    SyncScheduler.scheduleDailySync(context)
                }
                else -> {
                    Log.d(TAG, "Data is fresh – no sync needed today")
                }
            }

            _isReady.value = true
        }
    }

    companion object {
        private const val TAG = "SplashViewModel"
    }
}
