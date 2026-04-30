package com.swahilib.domain.repos

import android.content.*
import android.util.Log
import com.swahilib.core.di.DatabaseModule
import com.swahilib.data.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.sources.local.daos.SayingDao
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.SayingDto
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.collections.map

@Singleton
class SayingRepo @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var sayingsDao: SayingDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        sayingsDao = db.sayingsDao()
    }

    suspend fun fetchRemoteData() {
        try {
            Log.d("TAG", "Fetching sayings")
            val result = supabase[Collections.SAYINGS]
                .select()
                .decodeList<SayingDto>()

            if (result.isNotEmpty()) {
                val sayings = result.map { MapDtoToEntity.mapToEntity(it) }
                Log.d("TAG", "✅ ${sayings.size} sayings fetched")
                saveSayings(sayings)
            } else {
                Log.d("TAG", "⚠️ No sayings fetched from remote")
            }
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error fetching sayings: ${e.message}", e)
        }
    }

    suspend fun saveSayings(sayings: List<Saying>) {
        if (sayings.isEmpty()) {
            Log.d("TAG", "⚠️ No sayings to save")
            return
        }

        try {
            sayingsDao?.insertAll(sayings)
            Log.d("TAG", "✅ ${sayings.size} sayings saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving sayings: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<Saying> {
        return withContext(Dispatchers.IO) {
            sayingsDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveSaying(saying: Saying) {
        withContext(Dispatchers.IO) {
            sayingsDao?.insert(saying)
        }
    }

    suspend fun updateSaying(saying: Saying) {
        try {
            withContext(Dispatchers.IO) {
                sayingsDao?.update(saying)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    suspend fun searchSayingsByTitle(title: String?) {
//        sayingsDao?.searchSayingByTitle(title)?.map { it.asDomainModel() }
    }

    fun getSayingsByTitles(titles: List<String>): Flow<List<Saying>> {
        return sayingsDao?.getSayingsByTitles(titles) ?: flowOf(emptyList())
    }

    suspend fun getSayingById(sayingId: String): Flow<Saying> {
        try {
//            val sayingFlow = sayingsDao?.getById(sayingId)
//            return sayingFlow.map {
//                it.asDomainModel()
//            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}

