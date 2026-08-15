package com.swahilib.core.data.repos.content

import android.util.Log
import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.database.entities.content.WordEntity
import com.swahilib.core.network.api.KamusiApi
import com.swahilib.core.network.dtos.WordDto
import com.swahilib.core.network.mapper.MapDtoToEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepo @Inject constructor(
    private val wordsDao: WordDao,
    private val api: KamusiApi,
) {
    suspend fun fetchRemoteData(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val dtos = api.fetchJson<WordDto>(KamusiApi.Endpoint.WORDS)
                ?: return@runCatching 0
            val entities = dtos.map(MapDtoToEntity::mapToEntity)
            wordsDao.insertAll(entities)
            Log.d(TAG, "✅ ${entities.size} words seeded")
            entities.size
        }.onFailure { Log.e(TAG, "❌ fetchRemoteData failed: ${it.message}", it) }
    }

    suspend fun fetchLocalData(): List<WordEntity> = withContext(Dispatchers.IO) {
        wordsDao.getAll()?.first() ?: emptyList()
    }

    suspend fun saveWord(word: WordEntity) = withContext(Dispatchers.IO) {
        runCatching { wordsDao.insert(word) }
            .onFailure { Log.e(TAG, "saveWord: ${it.message}") }
    }

    suspend fun updateWord(word: WordEntity) = withContext(Dispatchers.IO) {
        runCatching { wordsDao.update(word) }
            .onFailure { Log.e(TAG, "updateWord: ${it.message}") }
    }

    fun getWordsByTitles(titles: List<String>): Flow<List<WordEntity>> =
        wordsDao.getWordsByTitles(titles) ?: flowOf(emptyList())

    suspend fun getRandomWord(): WordEntity? = withContext(Dispatchers.IO) {
        wordsDao.getRandomWord()
    }

    suspend fun getWordByRid(rid: Int): WordEntity? = withContext(Dispatchers.IO) {
        wordsDao.getByRid(rid)
    }

    suspend fun clearAllLikes() = withContext(Dispatchers.IO) {
        wordsDao.clearAllLiked()
    }

    companion object { private const val TAG = "WordRepo" }
}
