package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Per-game "kiwango" (level) progress. One row per [gameType] (a
 * `StatisticsEngine.EventType` name, e.g. "HANGMAN"). Tracks how far the
 * player has unlocked and how many points they've banked historically for
 * that game - the "Points in the game (gotten previously)" figure shown in
 * the top app bar.
 */
@Keep
@Entity(tableName = "game_level_progress")
data class GameLevelProgressEntity(
    @PrimaryKey @ColumnInfo(name = "gameType") val gameType: String,
    @ColumnInfo(name = "highestUnlockedLevel") val highestUnlockedLevel: Int = 1,
    @ColumnInfo(name = "totalPoints") val totalPoints: Long = 0L,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = 0L,
)
