package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.data.models.Saying
import kotlinx.coroutines.flow.Flow

@Dao
interface SayingDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(saying: Saying)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(sayings: List<Saying>)

    @Update
    fun update(saying: Saying)

    @Query("SELECT * FROM sayings WHERE rid = :rid")
    fun getById(rid: String): Flow<Saying>

    @Query("DELETE FROM sayings")
    fun delete()

    @Query("SELECT * FROM sayings WHERE title LIKE '%' || :title || '%'")
    fun searchSayingByTitle(title: String?): Flow<List<Saying>>

    @Query("SELECT * FROM sayings WHERE title IN (:titles)")
    fun getSayingsByTitles(titles: List<String>): Flow<List<Saying>>

    @Query("SELECT * FROM sayings")
    fun getAll(): Flow<List<Saying>>
}