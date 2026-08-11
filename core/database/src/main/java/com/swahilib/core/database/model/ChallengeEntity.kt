package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "challenges",
    indices = [
        Index(value = ["scope", "periodKey"], unique = true),
        Index(value = ["expiresAt"]),
    ],
)
data class ChallengeEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "scope") val scope: String,
    @ColumnInfo(name = "periodKey") val periodKey: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "difficulty") val difficulty: String,
    @ColumnInfo(name = "xpReward") val xpReward: Int,
    @ColumnInfo(name = "coinReward") val coinReward: Int,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "expiresAt") val expiresAt: Long,
    @ColumnInfo(name = "completed") val completed: Boolean = false,
    @ColumnInfo(name = "completedAt") val completedAt: Long? = null,
)
