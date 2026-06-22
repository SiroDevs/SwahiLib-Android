package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.network.api.KamusiApi
import com.swahilib.core.network.dtos.IdiomDto
import com.swahilib.core.network.mapper.MapDtoToEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IdiomRepo @Inject constructor(
    private val idiomsDao: IdiomDao,
    private val api: KamusiApi,
) {
    suspend fun fetchRemoteData(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val dtos = api.fetchJson<IdiomDto>(KamusiApi.Endpoint.IDIOMS)
                ?: return@runCatching 0
            val entities = dtos.map(MapDtoToEntity::mapToEntity)
            idiomsDao.insertAll(entities)
            Log.d(TAG, "✅ ${entities.size} idioms seeded")
            entities.size
        }.onFailure { Log.e(TAG, "❌ fetchRemoteData failed: ${it.message}", it) }
    }

    suspend fun fetchLocalData(): List<IdiomEntity> = withContext(Dispatchers.IO) {
        idiomsDao.getAll()?.first() ?: emptyList()
    }

    suspend fun saveIdiom(idiom: IdiomEntity) = withContext(Dispatchers.IO) {
        idiomsDao.insert(idiom)
    }

    suspend fun updateIdiom(idiom: IdiomEntity) = withContext(Dispatchers.IO) {
        runCatching { idiomsDao.update(idiom) }
            .onFailure { Log.e(TAG, "updateIdiom: ${it.message}") }
    }

    fun getIdiomsByTitles(titles: List<String>): Flow<List<IdiomEntity>> =
        idiomsDao.getIdiomsByTitles(titles)

    suspend fun getIdiomById(idiomId: String): Flow<IdiomEntity> = flow {}

    companion object { private const val TAG = "IdiomRepo" }
}
