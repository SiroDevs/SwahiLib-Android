package com.swahilib.core.database.entities.game

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "game_session_state")
data class GameSessionStateEntity(
    @PrimaryKey @ColumnInfo(name = "gameType") val gameType: String,
    @ColumnInfo(name = "level") val level: Int,
    @ColumnInfo(name = "contentSeed") val contentSeed: Long,
    @ColumnInfo(name = "stepIndex") val stepIndex: Int,
    @ColumnInfo(name = "livePoints") val livePoints: Int,
    @ColumnInfo(name = "snapshotJson") val snapshotJson: String,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = 0L,
)
