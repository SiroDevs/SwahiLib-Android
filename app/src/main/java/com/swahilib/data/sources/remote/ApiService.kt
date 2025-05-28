package com.swahilib.data.sources.remote

import androidx.annotation.Keep
import com.swahilib.core.utils.ApiConstants
import com.swahilib.data.models.*
import retrofit2.http.*

@Keep
interface ApiService {
    @GET(ApiConstants.BOOKS)
    suspend fun getWords(): List<Word>

}
