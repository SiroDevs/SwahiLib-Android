package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "achievement_records")
data class AchievementRecordEntity(
    @PrimaryKey val achievementId: String,
    @ColumnInfo(name = "unlockedAt") val unlockedAt: Long,
    @ColumnInfo(name = "xpAwarded") val xpAwarded: Int,
    @ColumnInfo(name = "coinsAwarded") val coinsAwarded: Int,
)
