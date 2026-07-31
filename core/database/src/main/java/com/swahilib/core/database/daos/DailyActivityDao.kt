package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.model.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfMissing(entity: DailyActivityEntity): Long

    @Update
    suspend fun update(entity: DailyActivityEntity)

    @Query("SELECT * FROM daily_activity WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyActivityEntity?

    @Query("SELECT * FROM daily_activity WHERE date BETWEEN :from AND :to ORDER BY date ASC")
    suspend fun getRange(from: String, to: String): List<DailyActivityEntity>

    @Query("SELECT * FROM daily_activity ORDER BY date DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<DailyActivityEntity>>

    @Query("SELECT COUNT(*) FROM daily_activity WHERE visited = 1 AND date BETWEEN :from AND :to")
    suspend fun activeDaysBetween(from: String, to: String): Int
}
