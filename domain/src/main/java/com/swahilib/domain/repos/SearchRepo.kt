package com.swahilib.domain.repos

import android.content.Context
import android.util.Log
import com.swahilib.data.di.DatabaseModule
import com.swahilib.data.models.Search
import com.swahilib.data.sources.local.daos.SearchDao
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepo @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var searchDao: SearchDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        searchDao = db.searchDao()
    }

    suspend fun fetchLocalData(): List<Search> {
        return withContext(Dispatchers.IO) {
            searchDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveSearch(search: Search) {
        withContext(Dispatchers.IO) {
            searchDao?.insert(search)
        }
    }

    suspend fun searchSearchsByTitle(title: String?) {
        //searchDao?.searchSearchByTitle(title)?.map { it.asDomainModel() }
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

