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
import com.swahilib.core.database.entities.game.GameLevelProgressEntity
import com.swahilib.core.database.entities.game.GameSessionStateEntity

@Dao
interface GameProgressDao {
    @Query("SELECT * FROM game_level_progress WHERE gameType = :gameType LIMIT 1")
    suspend fun getLevelProgress(gameType: String): GameLevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLevelProgress(entity: GameLevelProgressEntity)

    @Query("SELECT * FROM game_session_state WHERE gameType = :gameType LIMIT 1")
    suspend fun getSessionState(gameType: String): GameSessionStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSessionState(entity: GameSessionStateEntity)

    @Query("DELETE FROM game_session_state WHERE gameType = :gameType")
    suspend fun clearSessionState(gameType: String)

    @Query("DELETE FROM game_level_progress")
    suspend fun deleteAllProgress()

    @Query("DELETE FROM game_session_state")
    suspend fun deleteAllSessions()
}
