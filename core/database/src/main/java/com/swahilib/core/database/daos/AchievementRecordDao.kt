package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swahilib.core.database.model.AchievementRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementRecordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: AchievementRecordEntity): Long

    @Query("SELECT * FROM achievement_records WHERE achievementId = :id LIMIT 1")
    suspend fun getById(id: String): AchievementRecordEntity?

    @Query("SELECT * FROM achievement_records ORDER BY unlockedAt DESC")
    suspend fun getAll(): List<AchievementRecordEntity>

    @Query("SELECT * FROM achievement_records ORDER BY unlockedAt DESC")
    fun observeAll(): Flow<List<AchievementRecordEntity>>

    @Query("SELECT COUNT(*) FROM achievement_records")
    suspend fun count(): Int

    @Query("DELETE FROM achievement_records")
    suspend fun deleteAll()
}
