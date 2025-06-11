package com.swahilib.data.sources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.utils.Collections
import com.swahilib.data.models.Search
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(word: Search)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(products: List<Search>)

    @Update
    fun update(word: Search)

    @Query("SELECT * FROM ${Collections.WORDS} WHERE rid = :id")
    fun getById(id: String): Flow<Search>

    @Query("DELETE FROM ${Collections.WORDS}")
    fun delete()

    @Query("SELECT * FROM ${Collections.WORDS} WHERE title LIKE '%' || :title || '%'")
    fun searchSearchByTitle(title: String?): Flow<List<Search>>

    @Query("SELECT * FROM ${Collections.WORDS}")
    fun getAll(): Flow<List<Search>>
}