package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.data.models.History
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(history: History)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(histories: List<History>)

    @Update
    fun update(history: History)

    @Query("SELECT * FROM history WHERE id = :id")
    fun getById(id: String): Flow<History>

    @Query("DELETE FROM history")
    fun delete()

    @Query("SELECT * FROM history")
    fun getAll(): Flow<List<History>>
}