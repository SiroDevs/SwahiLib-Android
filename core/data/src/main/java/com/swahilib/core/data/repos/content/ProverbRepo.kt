package com.swahilib.core.data.repos.content

import android.util.Log
import com.swahilib.core.database.daos.content.ProverbDao
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.network.api.KamusiApi
import com.swahilib.core.network.dtos.ProverbDto
import com.swahilib.core.network.mapper.MapDtoToEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProverbRepo @Inject constructor(
    private val proverbsDao: ProverbDao,
    private val api: KamusiApi,
) {
    suspend fun fetchRemoteData(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val dtos = api.fetchJson<ProverbDto>(KamusiApi.Endpoint.PROVERBS)
                ?: return@runCatching 0
            val entities = dtos.map(MapDtoToEntity::mapToEntity)
            proverbsDao.insertAll(entities)
            Log.d(TAG, "✅ ${entities.size} proverbs seeded")
            entities.size
        }.onFailure { Log.e(TAG, "❌ fetchRemoteData failed: ${it.message}", it) }
    }

    suspend fun fetchLocalData(): List<ProverbEntity> = withContext(Dispatchers.IO) {
        proverbsDao.getAll().first() ?: emptyList()
    }

    suspend fun saveProverb(proverb: ProverbEntity) = withContext(Dispatchers.IO) {
        proverbsDao.insert(proverb)
    }

    suspend fun updateProverb(proverb: ProverbEntity) = withContext(Dispatchers.IO) {
        runCatching { proverbsDao.update(proverb) }
            .onFailure { Log.e(TAG, "updateProverb: ${it.message}") }
    }

    fun getProverbsByTitles(titles: List<String>): Flow<List<ProverbEntity>> =
        proverbsDao.getProverbsByTitles(titles)

    suspend fun getRandomProverb(): ProverbEntity? = withContext(Dispatchers.IO) {
        proverbsDao.getRandomProverb()
    }

    suspend fun getProverbByRid(rid: Int): ProverbEntity? = withContext(Dispatchers.IO) {
        proverbsDao.getByRid(rid)
    }

    suspend fun clearAllLikes() = withContext(Dispatchers.IO) {
        proverbsDao.clearAllLiked()
    }

    companion object { private const val TAG = "ProverbRepo" }
}
