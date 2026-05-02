package com.swahilib.domain.repos

import android.content.Context
import android.util.Log
import com.swahilib.data.di.DatabaseModule
import com.swahilib.data.models.Idiom
import com.swahilib.data.sources.local.daos.IdiomDao
import com.swahilib.data.sources.remote.MapDtoToEntity
import com.swahilib.data.sources.remote.dtos.IdiomDto
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class IdiomRepo @Inject constructor(
    context: Context,
    private val supabase: Postgrest,
)  {
    private var idiomDao: IdiomDao?

    init {
        val db = DatabaseModule.provideDatabase(context)
        idiomDao = db.idiomDao()
    }

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

    suspend fun saveIdioms(idioms: List<Idiom>) {
        if (idioms.isEmpty()) {
            Log.d("TAG", "⚠️ No idioms to save")
            return
        }

        try {
            idiomDao?.insertAll(idioms)
            Log.d("TAG", "✅ ${idioms.size} idioms saved successfully")
        } catch (e: Exception) {
            Log.e("TAG", "❌ Error saving idioms: ${e.message}", e)
            throw e
        }
    }

    suspend fun fetchLocalData(): List<Idiom> {
        return withContext(Dispatchers.IO) {
            idiomDao?.getAll()?.first() ?: emptyList()
        }
    }

    suspend fun saveIdiom(idiom: Idiom) {
        withContext(Dispatchers.IO) {
            idiomDao?.insert(idiom)
        }
    }

    suspend fun updateIdiom(idiom: Idiom) {
        try {
            withContext(Dispatchers.IO) {
                idiomDao?.update(idiom)
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }
}
