package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.common.utils.ApiConstants
import com.swahilib.core.network.services.PesaPalService
import com.swahilib.core.network.dtos.PesaPalAuthRequest
import com.swahilib.core.network.dtos.PesaPalBillingAddress
import com.swahilib.core.network.dtos.PesaPalOrderRequest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val TAG = "DonationRepo"

@Singleton
class DonationRepo @Inject constructor(
    private val pesaPalService: PesaPalService,
    @Named("pesapal_consumer_key") private val consumerKey: String,
    @Named("pesapal_consumer_secret") private val consumerSecret: String,
    @Named("pesapal_ipn_id") private val ipnId: String,
) {
    suspend fun submitDonation(
        amountUsd: Double
    ): Result<String> {
        return try {
            val authResponse = pesaPalService.requestAuthToken(
                PesaPalAuthRequest(
                    consumerKey = consumerKey,
                    consumerSecret = consumerSecret,
                )
            )
            val token = authResponse.token
            if (token.isNullOrBlank()) {
                Log.e(TAG, "❌ Auth failed: ${authResponse.message}")
                return Result.failure(Exception("Imeshindwa kupata ruhusa ya malipo"))
            }
            Log.d(TAG, "✅ Auth token obtained")

            val merchantRef = "SWAHILIB-${UUID.randomUUID().toString().take(8).uppercase()}"
            val orderResponse = pesaPalService.submitOrder(
                bearer = "Bearer $token",
                body = PesaPalOrderRequest(
                    id = merchantRef,
                    currency = "USD",
                    amount = amountUsd,
                    description = "Mchango kwa SwahiLib — Asante kwa msaada wako!",
                    callbackUrl = ApiConstants.CALLBACK_URL,
                    notificationId = ipnId,
                    billingAddress = PesaPalBillingAddress(emailAddress = ApiConstants.DONOR_EMAIL),
                ),
            )

            val redirectUrl = orderResponse.redirectUrl
            if (redirectUrl.isNullOrBlank()) {
                Log.e(TAG, "❌ Order submission failed — no redirect URL: ${orderResponse.message}")
                return Result.failure(Exception("Imeshindwa kuwasilisha ombi la malipo"))
            }

            Log.d(TAG, "✅ Order accepted — redirect URL: $redirectUrl")
            Result.success(redirectUrl)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Donation error: ${e.message}", e)
            Result.failure(e)
        }
    }
}
