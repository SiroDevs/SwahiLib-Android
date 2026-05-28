package com.swahilib.feature.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.helpers.NetworkUtils
import com.swahilib.core.data.repos.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDataLoaded = MutableStateFlow(prefsRepo.isDataLoaded)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    fun initialize(context: Context) {
        viewModelScope.launch {
            val installTime = prefsRepo.installDate
            if (installTime == 0L) {
                prefsRepo.installDate = System.currentTimeMillis()
            }

            _isDataLoaded.value = prefsRepo.isDataLoaded

            // If data is already loaded, check for updates in the background
            // This runs fast and transparently – no blocking UI
            if (prefsRepo.isDataLoaded && NetworkUtils.isNetworkAvailable(context)) {
                try {
                    val shouldUpdate = prefsRepo.hasTimeExceeded(hours = 6)
                    if (shouldUpdate) {
                        Log.d("TAG", "🔄 Checking for updates in background...")
                        val idiomUpdate = async { idiomRepo.fetchRemoteData() }
                        val proverbUpdate = async { proverbRepo.fetchRemoteData() }
                        val sayingUpdate = async { sayingRepo.fetchRemoteData() }
                        val wordUpdate = async { wordRepo.fetchRemoteData() }
                        idiomUpdate.await()
                        proverbUpdate.await()
                        sayingUpdate.await()
                        wordUpdate.await()
                        prefsRepo.updateAppOpenTime()
                        Log.d("TAG", "✅ Background update complete")
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "Background update failed: ${e.message}")
                }
            }

            _isLoading.value = false
        }
    }
}
