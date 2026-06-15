package com.swahilib.feature.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.DonationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DonationState {
    object Idle : DonationState()
    object Loading : DonationState()
    data class ReadyToPay(val redirectUrl: String) : DonationState()
    data class Error(val message: String) : DonationState()
}

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val donationRepo: DonationRepo,
) : ViewModel() {

    private val _state = MutableStateFlow<DonationState>(DonationState.Idle)
    val state: StateFlow<DonationState> = _state.asStateFlow()

    fun submitDonation(amountUsd: Double) {
        if (amountUsd <= 0) return
        _state.value = DonationState.Loading

        viewModelScope.launch {
            donationRepo
                .submitDonation(amountUsd)
                .onSuccess { redirectUrl ->
                    _state.value = DonationState.ReadyToPay(redirectUrl)
                }
                .onFailure { e ->
                    _state.value = DonationState.Error(
                        e.message ?: "Kuna tatizo. Jaribu tena."
                    )
                }
        }
    }

    fun resetState() {
        _state.value = DonationState.Idle
    }
}
