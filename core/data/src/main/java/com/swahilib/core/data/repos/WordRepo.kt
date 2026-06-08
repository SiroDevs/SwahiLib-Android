package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.network.dtos.WordDto
import com.swahilib.core.network.mapper.MapDtoToEntity
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepo @Inject constructor(
    private val wordsDao: WordDao,
    private val supabase: Postgrest,
) {

    suspend fun fetchRemoteData(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            var offset = 0L
            val pageSize = 2000
            val allWords = mutableListOf<WordEntity>()
            var totalFetched = 0

            while (true) {
                val batch = supabase["words"]
                    .select { range(offset, offset + pageSize - 1) }
                    .decodeList<WordDto>()

                if (batch.isEmpty()) break

                val mappedBatch = batch.map(MapDtoToEntity::mapToEntity)
                allWords.addAll(mappedBatch)
                totalFetched += batch.size

                if (batch.size < pageSize) break
                offset += pageSize
                if (offset % 10000 == 0L) delay(100)
            }

            saveWords(allWords)
            Result.success(totalFetched)
        }.getOrElse { exception ->
            Log.e("WordRepo", "❌ Error fetching words: ${exception.message}", exception)
            Result.failure(exception)
        }
    }

    suspend fun saveWords(words: List<WordEntity>) {
        if (words.isEmpty()) return
        try {
            wordsDao.insertAll(words)
        } catch (e: Exception) {
            Log.e("WordRepo", "❌ Error saving words: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<WordEntity> = withContext(Dispatchers.IO) {
        wordsDao.getAll()?.first() ?: emptyList()
    }

    suspend fun saveWord(word: WordEntity) {
        try { withContext(Dispatchers.IO) { wordsDao.insert(word) } }
        catch (e: Exception) { Log.d("WordRepo", e.message.toString()) }
    }

    suspend fun updateWord(word: WordEntity) {
        try { withContext(Dispatchers.IO) { wordsDao.update(word) } }
        catch (e: Exception) { Log.d("WordRepo", e.message.toString()) }
    }

    fun getWordsByTitles(titles: List<String>): Flow<List<WordEntity>> =
        wordsDao.getWordsByTitles(titles) ?: flowOf(emptyList())

    /** Returns a random word; used by Neno la Siku. */
    suspend fun getRandomWord(): WordEntity? = withContext(Dispatchers.IO) {
        wordsDao.getRandomWord()
    }
}
