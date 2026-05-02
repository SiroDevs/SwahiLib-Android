package com.swahilib.domain.repos

import android.util.Log
import com.swahilib.data.models.Saying
import com.swahilib.data.sources.local.daos.SayingDao
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.SayingDto
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class SayingRepo @Inject constructor(
    private val supabase: Postgrest,
    private val sayingDao: SayingDao,
)  {
    suspend fun fetchRemoteData() {
        try {
            Log.d("TAG", "Fetching sayings")
            val result = supabase["sayings"]
                .select()
                .decodeList<SayingDto>()

            if (result.isNotEmpty()) {
                val sayings = result.map { MapDtoToEntity.mapToEntity(it) }
                Log.d("TAG", "✅ ${sayings.size} sayings fetched")
                saveSayings(sayings)
            } else {
                Log.d("TAG", "⚠️ No sayings fetched from remote")
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching sayings: ${e.message}", e)
        }
    }

    suspend fun saveSayings(sayings: List<Saying>) {
        if (sayings.isEmpty()) {
            Log.d("TAG", "⚠️ No sayings to save")
            return
        }

        try {
            sayingDao?.insertAll(sayings)
            Log.d("TAG", "✅ ${sayings.size} sayings saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving sayings: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<Saying> {
        return withContext(Dispatchers.IO) {
            sayingDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveSaying(saying: Saying) {
        withContext(Dispatchers.IO) {
            sayingDao?.insert(saying)
        }
    }

    suspend fun updateSaying(saying: Saying) {
        try {
            withContext(Dispatchers.IO) {
                sayingDao?.update(saying)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun searchSayingsByTitle(title: String?) {
//        sayingDao?.searchSayingByTitle(title)?.map { it.asDomainModel() }
    }

    fun getSayingsByTitles(titles: List<String>): Flow<List<Saying>> {
        return sayingDao?.getSayingsByTitles(titles) ?: flowOf(emptyList())
    }

    suspend fun getSayingById(sayingId: String): Flow<Saying> {
        try {
//            val sayingFlow = sayingDao?.getById(sayingId)
//            return sayingFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

