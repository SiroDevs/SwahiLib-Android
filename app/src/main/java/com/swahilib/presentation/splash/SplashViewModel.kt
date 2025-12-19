package com.swahilib.presentation.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.helpers.NetworkUtils
import com.swahilib.domain.repos.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val subsRepo: SubsRepo,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDataLoaded = MutableStateFlow(prefsRepo.isDataLoaded)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    init {
        Log.d("SplashViewModel", "ViewModel created. Initial isDataLoaded: ${_isDataLoaded.value}")
        Log.d("SplashViewModel", "PrefsRepo.isDataLoaded: ${prefsRepo.isDataLoaded}")
    }

    fun initializeApp(context: Context) {
        viewModelScope.launch {
            val installTime = prefsRepo.installDate
            if (installTime < 0) {
                prefsRepo.installDate = System.currentTimeMillis()
            }
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    checkSubscriptionAndTime(true)
                } else {
                    checkSubscriptionAndTime(false)
                }
            } catch (e: Exception) {
                _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
            _isDataLoaded.value = prefsRepo.isDataLoaded
        }
    }

    private suspend fun checkSubscriptionAndTime(isOnline: Boolean) {
        if (!prefsRepo.isProUser && prefsRepo.hasTimeExceeded(5)) {
            subsRepo.isProUser(isOnline) { isActive ->
                prefsRepo.isProUser = isActive
                prefsRepo.canShowPaywall = !isActive
            }
        }
        prefsRepo.updateAppOpenTime()
    }
}