package com.swahilib.domain.repository

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.HistoryDao
import io.github.jan.supabase.postgrest.Postgrest

@Singleton
class HistoryRepository @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var historiesDao: HistoryDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        historiesDao = db.historiesDao()
    }

    fun fetchRemoteData(): Flow<List<History>> = flow {
        try {
            val historys = supabase[Collections.WORDS]
                .select().decodeList<History>()
            emit(historys)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun fetchLocalData(): List<History> {
        return withContext(Dispatchers.IO) {
            historiesDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveHistory(history: History) {
        withContext(Dispatchers.IO) {
            historiesDao?.insert(history)
        }
    }
    suspend fun getHistoryById(historyId: String): Flow<History> {
        try {
//            val historyFlow = historiesDao?.getById(historyId)
//            return historyFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

