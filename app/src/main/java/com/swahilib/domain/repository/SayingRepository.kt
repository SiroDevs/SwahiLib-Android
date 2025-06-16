package com.swahilib.domain.repository

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.SayingDao
import com.swahilib.data.sources.remote.EntityMapper
import com.swahilib.data.sources.remote.dtos.SayingDto
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.collections.map

@Singleton
class SayingRepository @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var sayingsDao: SayingDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        sayingsDao = db.sayingsDao()
    }

    fun fetchRemoteData(): Flow<List<Saying>> = flow {
        try {
            Log.d("TAG", "Now fetching sayings")
            val result = supabase[Collections.SAYINGS]
                .select().decodeList<SayingDto>()
            val sayings = result.map { EntityMapper.mapToEntity(it) }
            Log.d("TAG", "Fetched ${sayings.size} sayings")
            emit(sayings)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun fetchLocalData(): List<Saying> {
        return withContext(Dispatchers.IO) {
            sayingsDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveSaying(saying: Saying) {
        withContext(Dispatchers.IO) {
            sayingsDao?.insert(saying)
        }
    }

    suspend fun searchSayingsByTitle(title: String?) {
//        sayingsDao?.searchSayingByTitle(title)?.map { it.asDomainModel() }
    }

    suspend fun getSayingById(sayingId: String): Flow<Saying> {
        try {
//            val sayingFlow = sayingsDao?.getById(sayingId)
//            return sayingFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

