package com.swahilib.domain.repos

import android.content.Context
import android.util.Log
import com.swahilib.data.di.DatabaseModule
import com.swahilib.data.models.Proverb
import com.swahilib.data.sources.local.daos.ProverbDao
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.ProverbDto
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

@Singleton
class ProverbRepo @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var proverbDao: ProverbDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        proverbDao = db.proverbDao()
    }

    suspend fun fetchRemoteData() {
        try {
            Log.d("TAG", "Fetching proverbs")
            val result = supabase["proverbs"]
                .select()
                .decodeList<ProverbDto>()

            if (result.isNotEmpty()) {
                val proverbs = result.map { MapDtoToEntity.mapToEntity(it) }
                Log.d("TAG", "✅ ${proverbs.size} proverbs fetched")
                saveProverbs(proverbs)
            } else {
                Log.d("TAG", "⚠️ No proverbs fetched from remote")
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching proverbs: ${e.message}", e)
        }
    }

    suspend fun saveProverbs(proverbs: List<Proverb>) {
        if (proverbs.isEmpty()) {
            Log.d("TAG", "⚠️ No proverbs to save")
            return
        }

        try {
            proverbDao?.insertAll(proverbs)
            Log.d("TAG", "✅ ${proverbs.size} proverbs saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving proverbs: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<Proverb> {
        return withContext(Dispatchers.IO) {
            proverbDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveProverb(proverb: Proverb) {
        withContext(Dispatchers.IO) {
            proverbDao?.insert(proverb)
        }
    }

    suspend fun updateProverb(proverb: Proverb) {
        try {
            withContext(Dispatchers.IO) {
                proverbDao?.update(proverb)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun searchProverbsByTitle(title: String?) {
//        proverbDao?.searchProverbByTitle(title)?.map { it.asDomainModel() }
    }

    fun getProverbsByTitles(titles: List<String>): Flow<List<Proverb>> {
        return proverbDao?.getProverbsByTitles(titles) ?: flowOf(emptyList())
    }

    suspend fun getProverbById(proverbId: String): Flow<Proverb> {
        try {
//            val proverbFlow = proverbDao?.getById(proverbId)
//            return proverbFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

