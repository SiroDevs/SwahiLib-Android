package com.swahilib.core.data.repos

import com.swahilib.core.database.daos.content.HistoryDao
import com.swahilib.core.database.entities.content.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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
}

