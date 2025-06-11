package com.swahilib.data.sources.remote

import androidx.annotation.Keep
import com.swahilib.core.helpers.NetworkResult
import com.swahilib.core.helpers.safeSupabaseCall
import com.swahilib.data.models.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import retrofit2.http.*

@Keep
interface ApiService {
    suspend fun fetchIdioms(): NetworkResult<List<Word>>

    suspend fun fetchSayings(): NetworkResult<List<Saying>>

    suspend fun fetchProverbs(): NetworkResult<List<Proverb>>

    suspend fun fetchWords(): NetworkResult<List<Word>>
}


internal class ApiServiceImpl(
    private val supabase: SupabaseClient,
) : ApiService {
    override suspend fun fetchIdioms(): NetworkResult<List<Word>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchSayings(): NetworkResult<List<Saying>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchProverbs(): NetworkResult<List<Proverb>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchWords(): NetworkResult<List<Word>> {
        /*safeSupabaseCall {
            val request = mapOf()
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Teams.GETACCOUNTTEAMS,
                    parameters = request.asJsonObject(),
                )
                .decodeList()
        }*/
        TODO("Not yet implemented")
    }
}