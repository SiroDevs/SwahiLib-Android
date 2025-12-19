package com.swahilib.domain.repos

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.WordDao
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.WordDto
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.delay

@Singleton
class WordRepo @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
) {
    private var wordsDao: WordDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        wordsDao = db.wordsDao()
    }

    suspend fun fetchRemoteData(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            var offset = 0L
            val pageSize = 2000
            val allWords = mutableListOf<Word>()
            var totalFetched = 0

            while (true) {
                Log.d("TAG", "Fetching words batch: $offset to ${offset + pageSize - 1}")

                val batch = supabase[Collections.WORDS]
                    .select {
                        range(offset, offset + pageSize - 1)
                    }.decodeList<WordDto>()

                if (batch.isEmpty()) {
                    Log.d("TAG", "No more words to fetch")
                    break
                }

                val mappedBatch = batch.map(MapDtoToEntity::mapToEntity)
                allWords.addAll(mappedBatch)
                totalFetched += batch.size

                if (batch.size < pageSize) {
                    Log.d("TAG", "Last page reached with ${batch.size} items")
                    break
                }

                offset += pageSize

                if (offset % 10000 == 0L) {
                    delay(100)
                }
            }

            Log.d("TAG", "✅ $totalFetched words fetched")
            saveWords(allWords)

            Result.success(totalFetched)
        }.getOrElse { exception ->
            Log.e("TAG", "❌ Error fetching words: ${exception.message}", exception)
            Result.failure(exception)
        }
    }

    suspend fun saveWords(words: List<Word>) {
        if (words.isEmpty()) {
            Log.d("TAG", "⚠️ No words to save")
            return
        }

        try {
            wordsDao?.insertAll(words)
            Log.d("TAG", "✅ ${words.size} words saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving words: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<Word> {
        return withContext(Dispatchers.IO) {
            wordsDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveWord(word: Word) {
        try {
            withContext(Dispatchers.IO) {
                wordsDao?.insert(word)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun updateWord(word: Word) {
        try {
            withContext(Dispatchers.IO) {
                wordsDao?.update(word)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    fun getWordsByTitles(titles: List<String>): Flow<List<Word>> {
        return wordsDao?.getWordsByTitles(titles) ?: flowOf(emptyList())
    }
}
