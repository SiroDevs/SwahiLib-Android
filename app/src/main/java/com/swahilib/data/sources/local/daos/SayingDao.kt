package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.core.utils.Collections
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

    @Query("SELECT * FROM ${Collections.SAYINGS} WHERE rid = :id")
    fun getById(id: String): Flow<Saying>

    @Query("DELETE FROM ${Collections.SAYINGS}")
    fun delete()

    @Query("SELECT * FROM ${Collections.SAYINGS} WHERE title LIKE '%' || :title || '%'")
    fun searchSayingByTitle(title: String?): Flow<List<Saying>>

    @Query("SELECT * FROM ${Collections.SAYINGS}")
    fun getAll(): Flow<List<Saying>>
}