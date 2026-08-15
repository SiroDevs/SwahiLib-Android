package com.swahilib.core.database.entities.game

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "game_level_progress")
data class GameLevelProgressEntity(
    @PrimaryKey @ColumnInfo(name = "gameType") val gameType: String,
    @ColumnInfo(name = "highestUnlockedLevel") val highestUnlockedLevel: Int = 1,
    @ColumnInfo(name = "totalPoints") val totalPoints: Long = 0L,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = 0L,
)
