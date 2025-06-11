package com.swahilib.data.sources.local.daos

import androidx.room.*
import com.swahilib.core.utils.Collections
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

    @Query("SELECT * FROM ${Collections.HISTORY} WHERE rid = :id")
    fun getById(id: String): Flow<History>

    @Query("DELETE FROM ${Collections.HISTORY}")
    fun delete()

    @Query("SELECT * FROM ${Collections.HISTORY}")
    fun getAll(): Flow<List<History>>
}