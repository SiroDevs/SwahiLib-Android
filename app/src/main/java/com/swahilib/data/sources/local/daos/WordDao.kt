package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.core.utils.Collections
import com.swahilib.data.models.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(word: Word)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(words: List<Word>)

    @Update
    fun update(word: Word)

    @Query("SELECT * FROM ${Collections.WORDS} WHERE id = :id")
    fun getById(id: String): Flow<Word>

    @Query("DELETE FROM ${Collections.WORDS}")
    fun delete()

    @Query("SELECT * FROM ${Collections.WORDS} WHERE title LIKE '%' || :title || '%'")
    fun searchWordByTitle(title: String?): Flow<List<Word>>

    @Query("SELECT * FROM ${Collections.WORDS}")
    fun getAll(): Flow<List<Word>>
}