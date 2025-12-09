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

    fun fetchRemoteData(): Flow<List<Word>> = flow {
        var offset = 0L
        val pageSize = 2000
        val allWords = mutableListOf<Word>()

        try {
            while (true) {
                Log.d("TAG", "Now fetching words")
                val batch = supabase[Collections.WORDS]
                    .select {
                        range(offset, offset + pageSize - 1)
                    }.decodeList<WordDto>()
                if (batch.isEmpty()) break

                val mapped = batch.map(MapDtoToEntity::mapToEntity)
                allWords += mapped

                if (batch.size < pageSize) break // last batch
                offset += pageSize
            }
            Log.d("TAG", "Fetched ${allWords.size} words")
            emit(allWords)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
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
