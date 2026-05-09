package com.swahilib.core.data.repos

import android.util.Log
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.network.dtos.IdiomDto
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
class IdiomRepo @Inject constructor(
    private val idiomsDao: IdiomDao,
    private val supabase: Postgrest,
) {
    suspend fun fetchRemoteData() {
        try {
            Log.d("TAG", "Fetching idioms")
            val result = supabase["idioms"]
                .select()
                .decodeList<IdiomDto>()

            if (result.isNotEmpty()) {
                val idioms = result.map { MapDtoToEntity.mapToEntity(it) }
                Log.d("TAG", "✅ ${idioms.size} idioms fetched")
                saveIdioms(idioms)
            } else {
                Log.d("TAG", "⚠️ No idioms fetched from remote")
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching idioms: ${e.message}", e)
        }
    }

    suspend fun saveIdioms(idioms: List<IdiomEntity>) {
        if (idioms.isEmpty()) {
            Log.d("TAG", "⚠️ No idioms to save")
            return
        }

        try {
            idiomsDao.insertAll(idioms)
            Log.d("TAG", "✅ ${idioms.size} idioms saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving idioms: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<IdiomEntity> {
        return withContext(Dispatchers.IO) {
            idiomsDao.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveIdiom(idiom: IdiomEntity) {
        withContext(Dispatchers.IO) {
            idiomsDao.insert(idiom)
        }
    }

    suspend fun updateIdiom(idiom: IdiomEntity) {
        try {
            withContext(Dispatchers.IO) {
                idiomsDao.update(idiom)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun searchIdiomsByTitle(title: String?) {
//        idiomsDao.searchIdiomByTitle(title)?.map { it.asDomainModel() }
    }

    fun getIdiomsByTitles(titles: List<String>): Flow<List<IdiomEntity>> {
        return idiomsDao.getIdiomsByTitles(titles)
    }

    suspend fun getIdiomById(idiomId: String): Flow<IdiomEntity> {
        try {
//            val idiomFlow = idiomsDao.getById(idiomId)
//            return idiomFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

