package com.swahilib.domain.repository

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
import com.swahilib.data.sources.remote.EntityMapper
import com.swahilib.data.sources.remote.dtos.WordDto
import io.github.jan.supabase.postgrest.Postgrest
import java.util.stream.IntStream.range

@Singleton
class WordRepository @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
) {
    private var wordsDao: WordDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        wordsDao = db.wordsDao()
    }

    fun fetchRemoteData(): Flow<List<Word>> = flow {
        val pageSize = 1000
        val allWords = mutableListOf<WordDto>()
        var offset = 0

        while (true) {
            val batch = supabase[Collections.WORDS]
                .select {
                    range(offset, pageSize)
                }
                .decodeList<WordDto>()

            if (batch.isEmpty()) break

            allWords += batch
            offset += pageSize
        }

        emit(allWords.map { EntityMapper.mapToEntity(it) })
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

    suspend fun searchWordsByTitle(title: String?) {
//        wordsDao?.searchWordByTitle(title)?.map { it.asDomainModel() }
    }

    suspend fun getWordById(wordId: String): Flow<Word> {
        try {
//            val wordFlow = wordsDao?.getById(wordId)
//            return wordFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

