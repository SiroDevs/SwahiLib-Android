package com.swahilib.domain.repository

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.ProverbDao
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.ProverbDto
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.collections.map

@Singleton
class ProverbRepository @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var proverbsDao: ProverbDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        proverbsDao = db.proverbsDao()
    }

    fun fetchRemoteData(): Flow<List<Proverb>> = flow {
        try {
            Log.d("TAG", "Now fetching proverbs")
            val result = supabase[Collections.PROVERBS]
                .select().decodeList<ProverbDto>()
            val proverbs = result.map { MapDtoToEntity.mapToEntity(it) }
            Log.d("TAG", "Fetched ${proverbs.size} proverbs")
            emit(proverbs)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun fetchLocalData(): List<Proverb> {
        return withContext(Dispatchers.IO) {
            proverbsDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveProverb(proverb: Proverb) {
        withContext(Dispatchers.IO) {
            proverbsDao?.insert(proverb)
        }
    }

    suspend fun searchProverbsByTitle(title: String?) {
//        proverbsDao?.searchProverbByTitle(title)?.map { it.asDomainModel() }
    }

    fun getProverbsByTitles(titles: List<String>): Flow<List<Proverb>> {
        return proverbsDao?.getProverbsByTitles(titles) ?: flowOf(emptyList())
    }

    suspend fun getProverbById(proverbId: String): Flow<Proverb> {
        try {
//            val proverbFlow = proverbsDao?.getById(proverbId)
//            return proverbFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

