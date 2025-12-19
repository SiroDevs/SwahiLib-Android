package com.swahilib.domain.repos

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
class ProverbRepo @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var proverbsDao: ProverbDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        proverbsDao = db.proverbsDao()
    }

    suspend fun fetchRemoteData()  {
        try {
            Log.d("TAG", "Fetching proverbs")
            val result = supabase[Collections.PROVERBS]
                .select().decodeList<ProverbDto>()
            val proverbs = result.map { MapDtoToEntity.mapToEntity(it) }
            Log.d("TAG", "✅ ${proverbs.size} proverbs fetched")
            saveProverbs(proverbs)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun saveProverbs(proverbs: List<Proverb>) {
        withContext(Dispatchers.IO) {
            proverbs.forEachIndexed { index, proverb ->
                proverbsDao?.insert(proverb)
            }
        }
        Log.d("TAG", "✅ proverbs saved successfully")
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

    suspend fun updateProverb(proverb: Proverb) {
        try {
            withContext(Dispatchers.IO) {
                proverbsDao?.update(proverb)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
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

