package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(history: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(histories: List<HistoryEntity>)

    @Update
    fun update(history: HistoryEntity)

    @Query("SELECT * FROM history WHERE id = :id")
    fun getById(id: String): Flow<HistoryEntity>

    @Query("DELETE FROM history")
    fun delete()

    @Query("SELECT * FROM history")
    fun getAll(): Flow<List<HistoryEntity>>
}