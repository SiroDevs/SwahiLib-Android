package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.network.dtos.ProverbDto
import com.swahilib.core.network.mapper.MapDtoToEntity
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProverbRepo @Inject constructor(
    private val proverbsDao: ProverbDao,
    private val supabase: Postgrest,
) {
    suspend fun fetchRemoteData() {
        try {
            val result = supabase["proverbs"].select().decodeList<ProverbDto>()
            if (result.isNotEmpty()) {
                val proverbs = result.map { MapDtoToEntity.mapToEntity(it) }
                saveProverbs(proverbs)
            }
        } catch (e: Exception) {
            Log.e("ProverbRepo", "❌ Error fetching proverbs: ${e.message}", e)
        }
    }

    suspend fun saveProverbs(proverbs: List<ProverbEntity>) {
        if (proverbs.isEmpty()) return
        try {
            proverbsDao.insertAll(proverbs)
        } catch (e: Exception) {
            Log.e("ProverbRepo", "❌ Error saving proverbs: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<ProverbEntity> = withContext(Dispatchers.IO) {
        proverbsDao.getAll().first() ?: emptyList()
    }

    suspend fun saveProverb(proverb: ProverbEntity) = withContext(Dispatchers.IO) {
        proverbsDao.insert(proverb)
    }

    suspend fun updateProverb(proverb: ProverbEntity) {
        try { withContext(Dispatchers.IO) { proverbsDao.update(proverb) } }
        catch (e: Exception) { Log.d("ProverbRepo", e.message.toString()) }
    }

    fun getProverbsByTitles(titles: List<String>): Flow<List<ProverbEntity>> =
        proverbsDao.getProverbsByTitles(titles)

    suspend fun getProverbById(proverbId: String): Flow<ProverbEntity> = flow {}

    /** Returns a random proverb; used by Methali ya Siku. */
    suspend fun getRandomProverb(): ProverbEntity? = withContext(Dispatchers.IO) {
        proverbsDao.getRandomProverb()
    }
}
