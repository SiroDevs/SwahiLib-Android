package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val date: String,
    @ColumnInfo(name = "xpEarned") val xpEarned: Long = 0L,
    @ColumnInfo(name = "activitiesCompleted") val activitiesCompleted: Int = 0,
    @ColumnInfo(name = "secondsSpent") val secondsSpent: Long = 0L,
    @ColumnInfo(name = "quizzesCorrect") val quizzesCorrect: Int = 0,
    @ColumnInfo(name = "quizzesTotal") val quizzesTotal: Int = 0,
    @ColumnInfo(name = "wordsLearned") val wordsLearned: Int = 0,
    @ColumnInfo(name = "gamesPlayed") val gamesPlayed: Int = 0,
    @ColumnInfo(name = "challengesCompleted") val challengesCompleted: Int = 0,
    @ColumnInfo(name = "visited") val visited: Boolean = false,
)
