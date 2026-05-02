package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.data.models.Search
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(search: Search)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(searches: List<Search>)

    @Update
    fun update(search: Search)

    @Query("SELECT * FROM search WHERE id = :id")
    fun getById(id: String): Flow<Search>

    @Query("DELETE FROM search")
    fun delete()

    @Query("SELECT * FROM search WHERE title LIKE '%' || :title || '%'")
    fun searchSearchByTitle(title: String?): Flow<List<Search>>

    @Query("SELECT * FROM search")
    fun getAll(): Flow<List<Search>>
}