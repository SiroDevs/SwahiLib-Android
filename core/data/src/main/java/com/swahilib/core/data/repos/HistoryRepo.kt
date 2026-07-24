package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.database.daos.HistoryDao
import com.swahilib.core.database.model.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepo @Inject constructor(
    private val historiesDao: HistoryDao,
) {
    suspend fun fetchLocalData(): List<HistoryEntity> {
        return withContext(Dispatchers.IO) {
            historiesDao.getAll().first() ?: emptyList()
        }
    }

    suspend fun saveHistory(history: HistoryEntity) {
        withContext(Dispatchers.IO) {
            historiesDao.insert(history)
        }
    }

    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            historiesDao.delete()
        }
    }
    suspend fun getHistoryById(historyId: String): Flow<HistoryEntity> {
        try {
//            val historyFlow = historiesDao.getById(historyId)
//            return historyFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

