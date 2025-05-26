package com.swahilib.data.sources.local

import androidx.room.*
import com.swahilib.data.models.*

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): List<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Word)

    @Update()
    fun update(book: Word)

    @Delete()
    fun delete(book: Word)
}

@Dao
interface SongDao {
    @Query("SELECT * FROM songs")
    fun getAll(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: Song)

    @Update()
    fun update(song: Song)

    @Delete()
    fun delete(song: Song)
}