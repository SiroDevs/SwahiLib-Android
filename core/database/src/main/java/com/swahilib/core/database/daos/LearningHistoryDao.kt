package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.swahilib.core.database.model.LearningHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningHistoryDao {

    @Insert
    suspend fun insert(entry: LearningHistoryEntity): Long

    @Query("SELECT * FROM learning_history ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<LearningHistoryEntity>>

    @Query("SELECT * FROM learning_history WHERE date = :date ORDER BY createdAt DESC")
    suspend fun getByDate(date: String): List<LearningHistoryEntity>

    @Query("SELECT COUNT(*) FROM learning_history WHERE type = :type")
    suspend fun countByType(type: String): Int

    @Query("SELECT COALESCE(SUM(secondsSpent), 0) FROM learning_history")
    suspend fun totalSeconds(): Long
}
