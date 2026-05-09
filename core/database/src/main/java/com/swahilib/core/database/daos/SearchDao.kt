package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(search: SearchEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(searches: List<SearchEntity>)

    @Update
    fun update(search: SearchEntity)

    @Query("SELECT * FROM search WHERE id = :id")
    fun getById(id: String): Flow<SearchEntity>

    @Query("DELETE FROM search")
    fun delete()

    @Query("SELECT * FROM search WHERE title LIKE '%' || :title || '%'")
    fun searchSearchByTitle(title: String?): Flow<List<SearchEntity>>

    @Query("SELECT * FROM search")
    fun getAll(): Flow<List<SearchEntity>>
}