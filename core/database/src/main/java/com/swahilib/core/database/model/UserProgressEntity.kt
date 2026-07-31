package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Singleton-row store for the user's engagement totals. Uses the same
 * `id = SINGLETON_ID` pattern as DailyContentEntity so PagingSource /
 * arbitrary reads keep working.
 */
@Keep
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    @ColumnInfo(name = "totalXp") val totalXp: Long = 0L,
    @ColumnInfo(name = "coins") val coins: Long = 0L,
    @ColumnInfo(name = "level") val level: Int = 1,
    @ColumnInfo(name = "challengesCompleted") val challengesCompleted: Int = 0,
    @ColumnInfo(name = "activitiesCompleted") val activitiesCompleted: Int = 0,
    @ColumnInfo(name = "wordsLearned") val wordsLearned: Int = 0,
    @ColumnInfo(name = "gamesPlayed") val gamesPlayed: Int = 0,
    @ColumnInfo(name = "totalLearningSeconds") val totalLearningSeconds: Long = 0L,
    @ColumnInfo(name = "updatedAt") val updatedAt: Long = 0L,
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
