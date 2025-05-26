package com.swahilib.data.sources.remote

import androidx.annotation.Keep
import com.swahilib.core.utils.ApiConstants
import com.swahilib.data.models.*
import retrofit2.http.*

@Keep
interface ApiService {
    @GET(ApiConstants.BOOKS)
    suspend fun getBooks(): List<Word>

    @GET(ApiConstants.SONGS)
    suspend fun getSongs(
        @Query("books") books: String,
    ): List<Song>
}
