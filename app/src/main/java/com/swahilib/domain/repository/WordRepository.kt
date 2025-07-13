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
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.WordDto
import io.github.jan.supabase.postgrest.Postgrest

@Singleton
class WordRepository @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var wordsDao: WordDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        wordsDao = db.wordsDao()
    }

    fun fetchRemoteData(): Flow<List<Word>> = flow {
        try {
            Log.d("TAG", "Now fetching words")
            val result = supabase[Collections.WORDS]
                .select().decodeList<WordDto>()
            val words = result.map { MapDtoToEntity.mapToEntity(it) }
            Log.d("TAG", "Fetched ${words.size} words")
            emit(words)
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

    suspend fun searchWordsByTitle(title: String?) {
//        wordsDao?.searchWordByTitle(title)?.map { it.asDomainModel() }
    }

    fun getWordsByTitles(titles: List<String>): Flow<List<Word>> {
        return wordsDao?.getWordsByTitles(titles) ?: flowOf(emptyList())
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
