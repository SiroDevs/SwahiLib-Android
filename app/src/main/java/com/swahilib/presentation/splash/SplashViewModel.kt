package com.swahilib.presentation.splash

import android.util.Log
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.helpers.NetworkUtils
import com.swahilib.core.utils.PrefConstants
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

    private val _installTime = MutableStateFlow(0L)
    val installTime: StateFlow<Long> = _installTime.asStateFlow()

    fun initializeApp(context: Context) {
        viewModelScope.launch {
            val installTime = prefsRepo.installDate

            if (installTime == 0L) {
                prefsRepo.installDate = System.currentTimeMillis()
                _installTime.value = prefsRepo.installDate
            } else {
                _installTime.value = installTime
            }

            _isDataLoaded.value = prefsRepo.isDataLoaded

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