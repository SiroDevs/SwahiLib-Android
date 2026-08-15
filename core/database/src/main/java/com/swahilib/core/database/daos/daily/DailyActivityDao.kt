/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.database.daos.daily

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swahilib.core.database.entities.daily.DailyActivityEntity
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

    @Query("DELETE FROM daily_activity")
    suspend fun deleteAll()
}
