package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "learning_history",
    indices = [Index(value = ["date"]), Index(value = ["type"])]
)
data class LearningHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "referenceId") val referenceId: String? = null,
    @ColumnInfo(name = "score") val score: Int? = null,
    @ColumnInfo(name = "maxScore") val maxScore: Int? = null,
    @ColumnInfo(name = "xpEarned") val xpEarned: Int = 0,
    @ColumnInfo(name = "secondsSpent") val secondsSpent: Int = 0,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
)
