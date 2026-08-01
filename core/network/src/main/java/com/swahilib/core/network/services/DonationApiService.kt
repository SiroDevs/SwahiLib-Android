package com.swahilib.core.network.services

import androidx.annotation.Keep
import com.swahilib.core.common.utils.ApiConstants
import com.swahilib.core.network.dtos.PaystackInitializeRequest
import com.swahilib.core.network.dtos.PaystackInitializeResponse
import retrofit2.http.Body
import retrofit2.http.POST

@Keep
interface DonationApiService {
    @POST(ApiConstants.DONATION_INITIALIZE)
    suspend fun initializeTransaction(
        @Body body: PaystackInitializeRequest,
    ): PaystackInitializeResponse
}
