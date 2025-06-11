package com.swahilib.domain.repository

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import com.swahilib.data.sources.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.WordDao
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
            val words = supabase[Collections.WORDS]
                .select().decodeList<Word>()
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
        withContext(Dispatchers.IO) {
            wordsDao?.insert(word)
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

