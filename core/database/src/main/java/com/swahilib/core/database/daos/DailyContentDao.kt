package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swahilib.core.database.model.DailyContentEntity

@Dao
interface DailyContentDao {

    @Query("SELECT * FROM daily_content WHERE id = ${DailyContentEntity.SINGLETON_ID} LIMIT 1")
    suspend fun get(): DailyContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyContentEntity)
}
