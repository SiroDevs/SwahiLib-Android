package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Update
    fun update(word: WordEntity)

    @Query("SELECT * FROM words WHERE rid = :rid")
    fun getById(rid: String): Flow<WordEntity>

    @Query("DELETE FROM words")
    fun delete()

    @Query("SELECT * FROM words WHERE title LIKE '%' || :title || '%'")
    fun searchWordByTitle(title: String?): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE title IN (:titles)")
    fun getWordsByTitles(titles: List<String>): Flow<List<WordEntity>>

    @Query("SELECT * FROM words")
    fun getAll(): Flow<List<WordEntity>>
}