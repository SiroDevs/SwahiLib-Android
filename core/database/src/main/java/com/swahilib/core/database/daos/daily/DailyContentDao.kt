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
import com.swahilib.core.database.entities.daily.DailyContentEntity

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

    @Query("DELETE FROM daily_content")
    suspend fun deleteAll()
}
