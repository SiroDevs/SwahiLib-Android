/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.network.services

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