package com.swahilib.feature.donation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.DonationMethod
import com.swahilib.core.data.repos.utils.DonationRepo
import com.swahilib.core.data.repos.utils.PrefsRepo
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
    private val prefsRepo: PrefsRepo,
) : ViewModel() {

    private val _state = MutableStateFlow<DonationState>(DonationState.Idle)
    val state: StateFlow<DonationState> = _state.asStateFlow()

    fun submitDonation(
        amountUsd: Double,
        donorName: String? = null,
        donorEmail: String? = null,
    ) {
        if (amountUsd <= 0) return
        _state.value = DonationState.Loading

        viewModelScope.launch {
            val donationAmount = calculateDonationWithFee(amountUsd)

            donationRepo
                .submitDonation(
                    amountUsd = donationAmount.total,
                    donorName = donorName?.trim()?.takeIf { it.isNotBlank() },
                    donorEmail = donorEmail?.trim()?.takeIf { it.isNotBlank() },
                )
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

    private fun calculateDonationWithFee(amountKes: Double): DonationAmount {
        val fee = when {
            amountKes in 200.0..1501.0 -> 20.0
            amountKes in 1501.0..20000.0 -> 40.0
            amountKes in 20001.0 ..100000.0 -> 60.0
            amountKes >= 100001.0 -> 200.0
            else -> 0.0
        }

        return DonationAmount(
            originalAmount = amountKes,
            fee = fee,
            total = amountKes + fee
        )
    }

    fun resetState() {
        _state.value = DonationState.Idle
    }

    fun recordCryptoDonation() {
        prefsRepo.recordDonation(DonationMethod.CRYPTO)
    }
}

data class DonationAmount(
    val originalAmount: Double,
    val fee: Double,
    val total: Double
)
