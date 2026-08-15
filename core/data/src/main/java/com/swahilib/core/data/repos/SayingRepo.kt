package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.database.daos.content.SayingDao
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.network.api.KamusiApi
import com.swahilib.core.network.dtos.SayingDto
import com.swahilib.core.network.mapper.MapDtoToEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SayingRepo @Inject constructor(
    private val sayingsDao: SayingDao,
    private val api: KamusiApi,
) {
    suspend fun fetchRemoteData(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val dtos = api.fetchJson<SayingDto>(KamusiApi.Endpoint.SAYINGS)
                ?: return@runCatching 0
            val entities = dtos.map(MapDtoToEntity::mapToEntity)
            sayingsDao.insertAll(entities)
            Log.d(TAG, "✅ ${entities.size} sayings seeded")
            entities.size
        }.onFailure { Log.e(TAG, "❌ fetchRemoteData failed: ${it.message}", it) }
    }

    suspend fun fetchLocalData(): List<SayingEntity> = withContext(Dispatchers.IO) {
        sayingsDao.getAll().first() ?: emptyList()
    }

    suspend fun saveSaying(saying: SayingEntity) = withContext(Dispatchers.IO) {
        sayingsDao.insert(saying)
    }

    suspend fun updateSaying(saying: SayingEntity) = withContext(Dispatchers.IO) {
        runCatching { sayingsDao.update(saying) }
            .onFailure { Log.e(TAG, "updateSaying: ${it.message}") }
    }

    fun getSayingsByTitles(titles: List<String>): Flow<List<SayingEntity>> =
        sayingsDao.getSayingsByTitles(titles)

    suspend fun getSayingByRid(rid: Int): SayingEntity? = withContext(Dispatchers.IO) {
        sayingsDao.getByRid(rid)
    }

    suspend fun clearAllLikes() = withContext(Dispatchers.IO) {
        sayingsDao.clearAllLiked()
    }

    companion object { private const val TAG = "SayingRepo" }
}
