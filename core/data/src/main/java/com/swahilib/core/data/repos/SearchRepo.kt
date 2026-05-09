package com.swahilib.core.data.repos

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.swahilib.core.database.daos.SearchDao
import com.swahilib.core.database.model.SearchEntity
import io.github.jan.supabase.postgrest.Postgrest

@Singleton
class SearchRepo @Inject constructor(
    private val searchesDao: SearchDao,
    private val supabase: Postgrest,
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

    suspend fun searchSearchsByTitle(title: String?) {
        //searchesDao.searchSearchByTitle(title)?.map { it.asDomainModel() }
    }

    suspend fun getSearchById(searchId: String): Flow<SearchEntity> {
        try {
//            val searchFlow = searchsDao?.getById(searchId)
//            return searchFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

