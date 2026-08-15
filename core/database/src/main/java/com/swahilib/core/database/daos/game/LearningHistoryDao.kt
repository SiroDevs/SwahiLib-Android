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
import androidx.room.Query
import com.swahilib.core.database.entities.game.LearningHistoryEntity
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

    @Query("SELECT * FROM learning_history WHERE type = :type ORDER BY createdAt DESC LIMIT :limit")
    suspend fun recentByType(type: String, limit: Int): List<LearningHistoryEntity>

    @Query("SELECT COUNT(*) FROM learning_history WHERE type = :type AND maxScore > 0 AND score = maxScore")
    suspend fun countPerfectByType(type: String): Int

    @Query("SELECT DISTINCT type FROM learning_history WHERE maxScore > 0 AND score = maxScore")
    suspend fun typesWithAtLeastOnePerfect(): List<String>

    @Query("SELECT COALESCE(SUM(secondsSpent), 0) FROM learning_history")
    suspend fun totalSeconds(): Long

    @Query("DELETE FROM learning_history")
    suspend fun deleteAll()
}
