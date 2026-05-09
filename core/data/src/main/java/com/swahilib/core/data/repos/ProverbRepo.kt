package com.swahilib.core.data.repos

import android.util.Log
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.network.mapper.MapDtoToEntity
import com.swahilib.core.network.dtos.ProverbDto

@Singleton
class ProverbRepo @Inject constructor(
    private val proverbsDao: ProverbDao,
    private val supabase: Postgrest,
) {
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

    suspend fun saveProverbs(proverbs: List<ProverbEntity>) {
        if (proverbs.isEmpty()) {
            Log.d("TAG", "⚠️ No proverbs to save")
            return
        }

        try {
            proverbsDao.insertAll(proverbs)
            Log.d("TAG", "✅ ${proverbs.size} proverbs saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving proverbs: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<ProverbEntity> {
        return withContext(Dispatchers.IO) {
            proverbsDao.getAll().first() ?: emptyList()
        }
    }

    suspend fun saveProverb(proverb: ProverbEntity) {
        withContext(Dispatchers.IO) {
            proverbsDao.insert(proverb)
        }
    }

    suspend fun updateProverb(proverb: ProverbEntity) {
        try {
            withContext(Dispatchers.IO) {
                proverbsDao.update(proverb)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun searchProverbsByTitle(title: String?) {
//        proverbsDao.searchProverbByTitle(title)?.map { it.asDomainModel() }
    }

    fun getProverbsByTitles(titles: List<String>): Flow<List<ProverbEntity>> {
        return proverbsDao.getProverbsByTitles(titles)
    }

    suspend fun getProverbById(proverbId: String): Flow<ProverbEntity> {
        try {
//            val proverbFlow = proverbsDao.getById(proverbId)
//            return proverbFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

