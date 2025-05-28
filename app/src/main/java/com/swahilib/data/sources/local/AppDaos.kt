package com.swahilib.data.sources.local

import androidx.room.*
import com.swahilib.data.models.*

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAll(): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Word)

    @Update()
    fun update(book: Word)

    @Delete()
    fun delete(book: Word)
}
