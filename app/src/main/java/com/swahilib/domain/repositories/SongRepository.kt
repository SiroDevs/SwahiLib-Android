package com.swahilib.domain.repositories

import android.content.*
import androidx.core.content.edit
import com.swahilib.core.utils.PrefConstants
import com.swahilib.data.models.*
import com.swahilib.data.sources.local.*
import com.swahilib.data.sources.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.*

@Singleton
class SongRepository @Inject constructor(
    context: Context,
    private val apiService: ApiService
) {
    private val prefs = context.getSharedPreferences(PrefConstants.PREFERENCE_FILE, Context.MODE_PRIVATE)

    private var songsDao: SongDao?

    init {
        val db = AppDatabase.getDatabase(context)
        songsDao = db?.songsDao()
    }

    fun getSongs(books: String): Flow<List<Song>> = flow {
        val songs = apiService.getSongs(books)
        emit(songs)
    }

    suspend fun saveSong(song: Song) {
        withContext(Dispatchers.IO) {
            songsDao?.insert(song)
        }
    }

    suspend fun updateSong(song: Song) {
        withContext(Dispatchers.IO) {
            songsDao?.update(song)
        }
    }

    fun getSelectedBookIds(): String? {
        return prefs.getString(PrefConstants.SELECTED_BOOKS, "")
    }

    suspend fun getAllSongs(): List<Song> {
        var allSongs: List<Song>
        withContext(Dispatchers.IO) {
            allSongs = songsDao?.getAll() ?: emptyList()
        }
        return allSongs
    }

    fun savePrefs() {
        prefs.edit { putBoolean(PrefConstants.DATA_LOADED, true) }
    }

}