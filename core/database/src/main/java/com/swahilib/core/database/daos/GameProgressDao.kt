package com.swahilib.core.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swahilib.core.database.model.GameLevelProgressEntity
import com.swahilib.core.database.model.GameSessionStateEntity

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
