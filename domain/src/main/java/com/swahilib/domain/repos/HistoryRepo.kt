package com.swahilib.domain.repos

import android.util.Log
import com.swahilib.data.models.History
import com.swahilib.data.sources.local.daos.HistoryDao
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@Singleton
class HistoryRepo @Inject constructor(
    private val supabase: Postgrest,
    private val historyDao: HistoryDao,
)  {
    fun fetchRemoteData(): Flow<List<History>> = flow {
        try {
            val histories = supabase["words"]
                .select().decodeList<History>()
            emit(histories)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun fetchLocalData(): List<History> {
        return withContext(Dispatchers.IO) {
            historyDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveHistory(history: History) {
        withContext(Dispatchers.IO) {
            historyDao?.insert(history)
        }
    }
    suspend fun getHistoryById(historyId: String): Flow<History> {
        try {
//            val historyFlow = historyDao?.getById(historyId)
//            return historyFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

