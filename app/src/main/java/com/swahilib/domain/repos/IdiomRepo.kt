package com.swahilib.domain.repos

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.IdiomDao
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.IdiomDto
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.collections.map

@Singleton
class IdiomRepo @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var idiomsDao: IdiomDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        idiomsDao = db.idiomsDao()
    }

    fun fetchRemoteData(): Flow<List<Idiom>> = flow {
        try {
            Log.d("TAG", "Now fetching idioms")
            val result = supabase[Collections.IDIOMS]
                .select().decodeList<IdiomDto>()
            val idioms = result.map { MapDtoToEntity.mapToEntity(it) }
            Log.d("TAG", "Fetched ${idioms.size} idioms")
            emit(idioms)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun fetchLocalData(): List<Idiom> {
        return withContext(Dispatchers.IO) {
            idiomsDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveIdioms(idioms: List<Idiom>) {
        withContext(Dispatchers.IO) {
            idioms.forEachIndexed { index, idiom ->
                idiomsDao?.insert(idiom)
            }
        }
    }

    suspend fun saveIdiom(idiom: Idiom) {
        withContext(Dispatchers.IO) {
            idiomsDao?.insert(idiom)
        }
    }

    suspend fun updateIdiom(idiom: Idiom) {
        try {
            withContext(Dispatchers.IO) {
                idiomsDao?.update(idiom)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun searchIdiomsByTitle(title: String?) {
//        idiomsDao?.searchIdiomByTitle(title)?.map { it.asDomainModel() }
    }

    fun getIdiomsByTitles(titles: List<String>): Flow<List<Idiom>> {
        return idiomsDao?.getIdiomsByTitles(titles) ?: flowOf(emptyList())
    }

    suspend fun getIdiomById(idiomId: String): Flow<Idiom> {
        try {
//            val idiomFlow = idiomsDao?.getById(idiomId)
//            return idiomFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

