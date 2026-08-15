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
import com.swahilib.core.database.entities.game.XpEventEntity

@Dao
interface XpEventDao {
    @Insert
    suspend fun insert(event: XpEventEntity): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events")
    suspend fun totalXp(): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events WHERE date = :date")
    suspend fun xpOn(date: String): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM xp_events WHERE date BETWEEN :from AND :to")
    suspend fun xpBetween(from: String, to: String): Long

    @Query("SELECT * FROM xp_events ORDER BY createdAt DESC LIMIT :limit")
    suspend fun recent(limit: Int = 50): List<XpEventEntity>

    @Query("DELETE FROM xp_events")
    suspend fun deleteAll()
}
