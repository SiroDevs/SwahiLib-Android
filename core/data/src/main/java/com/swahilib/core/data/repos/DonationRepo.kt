package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.common.utils.ApiConstants
import com.swahilib.core.network.dtos.PaystackCustomField
import com.swahilib.core.network.dtos.PaystackInitializeRequest
import com.swahilib.core.network.dtos.PaystackMetadata
import com.swahilib.core.network.services.PaystackService
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.roundToLong

private const val TAG = "DonationRepo"

@Singleton
class DonationRepo @Inject constructor(
    private val paystackService: PaystackService,
    @Named("paystack_secret_key") private val secretKey: String,
) {
    suspend fun submitDonation(amountUsd: Double): Result<String> {
        return try {
            val amountInCents = (amountUsd * 100).roundToLong()
            val reference = "SONGLIB-${UUID.randomUUID().toString().take(8).uppercase()}"

            val response = paystackService.initializeTransaction(
                bearer = "Bearer $secretKey",
                body = PaystackInitializeRequest(
                    email = ApiConstants.DONOR_EMAIL,
                    amount = amountInCents,
                    currency = "USD",
                    callbackUrl = ApiConstants.PAYSTACK_CALLBACK_URL,
                    metadata = PaystackMetadata(
                        customFields = listOf(
                            PaystackCustomField(
                                displayName = "App",
                                variableName = "app",
                                value = "SwahiLib",
                            ),
                            PaystackCustomField(
                                displayName = "Reference",
                                variableName = "reference",
                                value = reference,
                            ),
                        )
                    ),
                ),
            )

            val authUrl = response.data?.authorizationUrl
            if (!response.status || authUrl.isNullOrBlank()) {
                Log.e(TAG, "❌ Paystack init failed: ${response.message}")
                return Result.failure(Exception(response.message ?: "Imeshindwa kuanzisha malipo"))
            }

            Log.d(TAG, "✅ Paystack transaction initialized — URL: $authUrl")
            Result.success(authUrl)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Hitilafu ya Mchango: ${e.message}", e)
            Result.failure(e)
        }
    }
}
