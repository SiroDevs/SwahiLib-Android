package com.swahilib.domain.repository

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.SearchDao
import io.github.jan.supabase.postgrest.Postgrest

@Singleton
class SearchRepository @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var searchesDao: SearchDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        searchesDao = db.searchesDao()
    }

    suspend fun fetchLocalData(): List<Search> {
        return withContext(Dispatchers.IO) {
            searchesDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveSearch(search: Search) {
        withContext(Dispatchers.IO) {
            searchesDao?.insert(search)
        }
    }

    suspend fun searchSearchsByTitle(title: String?) {
        //searchesDao?.searchSearchByTitle(title)?.map { it.asDomainModel() }
    }

    suspend fun getSearchById(searchId: String): Flow<Search> {
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

