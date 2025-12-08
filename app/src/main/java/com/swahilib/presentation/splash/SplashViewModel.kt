package com.swahilib.presentation.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.helpers.NetworkUtils
import com.swahilib.domain.repository.PreferencesRepository
import com.swahilib.domain.repository.SubscriptionsRepository
import com.swahilib.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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