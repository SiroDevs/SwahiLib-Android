package com.swahilib.domain.repositories

import android.content.*
import com.swahilib.core.utils.PrefConstants
import com.swahilib.data.models.*
import com.swahilib.data.sources.local.*
import com.swahilib.data.sources.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.*
import androidx.core.content.edit

@Singleton
class WordRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService
)  {
    private val prefs = context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)

    private var wordsDao: WordDao?

    init {
        val db = AppDatabase.getDatabase(context)
        wordsDao = db?.wordsDao()
    }

    fun getWords(): Flow<List<Word>> = flow {
        val words = apiService.getWords()
        emit(words)
    }

    suspend fun saveWord(word: Word) {
        withContext(Dispatchers.IO) {
            wordsDao?.insert(word)
        }
    }

    suspend fun getAllWords(): List<Word> {
        var allWords: List<Word>
        withContext(Dispatchers.IO) {
            allWords = wordsDao?.getAll() ?: emptyList()
        }
        return allWords
    }

    fun savePrefs() {
        prefs.edit { putBoolean(PrefConstants.DATA_SELECTED, true) }
    }
}