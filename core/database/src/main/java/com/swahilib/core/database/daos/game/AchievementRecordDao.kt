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

package com.swahilib.core.database.daos.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swahilib.core.database.entities.game.AchievementRecordEntity
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
