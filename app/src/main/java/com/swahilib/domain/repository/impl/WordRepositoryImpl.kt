package com.swahilib.domain.repository.impl

import android.util.Log
import com.swahilib.core.utils.Collections
import com.swahilib.data.models.Word
import com.swahilib.data.models.asDomainModel
import com.swahilib.domain.models.WordModel
import com.swahilib.data.sources.local.daos.WordDao
import com.swahilib.data.sources.remote.SupabaseMapper
import com.swahilib.data.sources.remote.dtos.WordDto
import com.swahilib.domain.repository.WordRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val supabasePostgrest: Postgrest
) : WordRepository {

    override val words: Flow<List<WordModel>> =
        wordDao.getAll().map {
            it.asDomainModel()
        }

    override suspend fun fetchRemoteData() {
        try {
            val result = supabasePostgrest[Collections.WORDS]
                .select().decodeList<WordDto>()
            val words = result.map { SupabaseMapper.mapToEntity(it) }
            wordDao.insertAll(words)
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    override fun searchWordsByTitle(title: String?) =
        wordDao.searchWordByTitle(title).map { it.asDomainModel() }

    override fun getWordById(wordId: String): Flow<WordModel> {
        try {
            val wordFlow = wordDao.getById(wordId)
            return wordFlow.map {
                it.asDomainModel()
            }
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
        return flow {}
    }

}
