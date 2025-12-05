package com.swahilib.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.swahilib.core.helpers.NetworkUtils
import com.swahilib.domain.repository.*
import com.swahilib.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefsRepo: PreferencesRepository,
    private val subsRepo: SubscriptionsRepository,
) : ViewModel() {
    private val _nextRoute = MutableStateFlow(Routes.SPLASH)
    val nextRoute: StateFlow<String> = _nextRoute.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
