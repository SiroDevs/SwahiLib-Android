package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swahilib.core.database.model.DailyContentEntity

@Dao
interface DailyContentDao {

    @Query("SELECT * FROM daily_content WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyContentEntity?

    @Query("SELECT * FROM daily_content ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(): DailyContentEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: DailyContentEntity): Long

    @Query("SELECT * FROM daily_content ORDER BY date DESC")
    suspend fun getAll(): List<DailyContentEntity>
}
