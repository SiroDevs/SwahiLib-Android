package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "challenge_activities",
    indices = [
        Index(value = ["challengeId"]),
        Index(value = ["challengeId", "orderIndex"], unique = true),
    ],
)
data class ChallengeActivityEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "challengeId") val challengeId: String,
    @ColumnInfo(name = "orderIndex") val orderIndex: Int,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "estimatedSeconds") val estimatedSeconds: Int,
    @ColumnInfo(name = "xpReward") val xpReward: Int,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "completedAt") val completedAt: Long? = null,
)
