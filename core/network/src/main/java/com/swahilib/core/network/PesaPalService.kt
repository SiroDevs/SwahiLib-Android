package com.swahilib.core.network

import androidx.annotation.Keep
import com.swahilib.core.common.utils.ApiConstants
import com.swahilib.core.network.dtos.PesaPalAuthRequest
import com.swahilib.core.network.dtos.PesaPalAuthResponse
import com.swahilib.core.network.dtos.PesaPalOrderRequest
import com.swahilib.core.network.dtos.PesaPalOrderResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

@Keep
interface PesaPalService {
    @POST(ApiConstants.PESAPAL_AUTH)
    suspend fun requestAuthToken(
        @Body body: PesaPalAuthRequest,
    ): PesaPalAuthResponse

    @POST(ApiConstants.PESAPAL_ORDER)
    suspend fun submitOrder(
        @Header("Authorization") bearer: String,
        @Body body: PesaPalOrderRequest,
    ): PesaPalOrderResponse
}
