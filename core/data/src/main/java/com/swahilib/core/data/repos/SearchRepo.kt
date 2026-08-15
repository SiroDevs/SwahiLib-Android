package com.swahilib.core.data.repos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.swahilib.core.database.daos.content.SearchDao
import com.swahilib.core.database.entities.content.SearchEntity

@Singleton
class SearchRepo @Inject constructor(
    private val searchesDao: SearchDao,
) {
    suspend fun fetchLocalData(): List<SearchEntity> {
        return withContext(Dispatchers.IO) {
            searchesDao.getAll().first() ?: emptyList()
        }
    }

    suspend fun saveSearch(search: SearchEntity) {
        withContext(Dispatchers.IO) {
            searchesDao.insert(search)
        }
    }

    suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            searchesDao.delete()
        }
    }
}

