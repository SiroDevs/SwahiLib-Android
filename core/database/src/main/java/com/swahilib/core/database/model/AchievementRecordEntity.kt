package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A row exists here only for achievements the user has actually unlocked.
 * The definitive catalog (title, description, reward) lives in
 * `core:engagement` AchievementCatalog - this table just records "unlocked
 * when" so we survive an app upgrade that reshapes the catalog.
 */
@Keep
@Entity(tableName = "achievement_records")
data class AchievementRecordEntity(
    @PrimaryKey val achievementId: String,
    @ColumnInfo(name = "unlockedAt") val unlockedAt: Long,
    @ColumnInfo(name = "xpAwarded") val xpAwarded: Int,
    @ColumnInfo(name = "coinsAwarded") val coinsAwarded: Int,
)
