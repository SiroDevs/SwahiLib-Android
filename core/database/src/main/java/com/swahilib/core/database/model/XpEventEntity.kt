package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "xp_events",
    indices = [Index(value = ["date"]), Index(value = ["source"])]
)
data class XpEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "activityType") val activityType: String? = null,
    @ColumnInfo(name = "referenceId") val referenceId: String? = null,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "secondsSpent") val secondsSpent: Int = 0,
)
