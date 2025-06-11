package com.swahilib.data.sources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.utils.Collections
import com.swahilib.data.models.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface SayingDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(word: Word)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(products: List<Word>)

    @Update
    fun update(word: Word)

    @Query("SELECT * FROM ${Collections.WORDS} WHERE rid = :id")
    fun getById(id: String): Flow<Word>

    @Query("DELETE FROM ${Collections.WORDS}")
    fun delete()

    @Query("SELECT * FROM ${Collections.WORDS} WHERE title LIKE '%' || :title || '%'")
    fun searchWordByTitle(title: String?): Flow<List<Word>>

    @Query("SELECT * FROM ${Collections.WORDS}")
    fun getAll(): Flow<List<Word>>
}