package com.swahilib.core.data.repos

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.swahilib.core.database.daos.SayingDao
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.network.mapper.MapDtoToEntity
import io.github.jan.supabase.postgrest.Postgrest
import com.swahilib.core.network.dtos.SayingDto

@Singleton
class SayingRepo @Inject constructor(
    private val sayingsDao: SayingDao,
    private val supabase: Postgrest,
) {
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

    suspend fun saveSayings(sayings: List<SayingEntity>) {
        if (sayings.isEmpty()) {
            Log.d("TAG", "⚠️ No sayings to save")
            return
        }

        try {
            sayingsDao.insertAll(sayings)
            Log.d("TAG", "✅ ${sayings.size} sayings saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving sayings: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<SayingEntity> {
        return withContext(Dispatchers.IO) {
            sayingsDao.getAll().first() ?: emptyList()
        }
    }

    suspend fun saveSaying(saying: SayingEntity) {
        withContext(Dispatchers.IO) {
            sayingsDao.insert(saying)
        }
    }

    suspend fun updateSaying(saying: SayingEntity) {
        try {
            withContext(Dispatchers.IO) {
                sayingsDao.update(saying)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun searchSayingsByTitle(title: String?) {
//        sayingsDao.searchSayingByTitle(title)?.map { it.asDomainModel() }
    }

    fun getSayingsByTitles(titles: List<String>): Flow<List<SayingEntity>> {
        return sayingsDao.getSayingsByTitles(titles)
    }

    suspend fun getSayingById(sayingId: String): Flow<SayingEntity> {
        try {
//            val sayingFlow = sayingsDao.getById(sayingId)
//            return sayingFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

