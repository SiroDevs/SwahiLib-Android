package com.swahilib.core.database.entities.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "punctuation_usage",
    foreignKeys = [
        ForeignKey(
            entity = PunctuationEntity::class,
            parentColumns = ["id"],
            childColumns = ["punctuationId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["punctuationId"])],
)
data class PunctuationUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "punctuationId") val punctuationId: Long,
    @ColumnInfo(name = "usage") val usage: String,
    @ColumnInfo(name = "example") val example: String? = null,
    @ColumnInfo(name = "orderIndex") val orderIndex: Int = 0,
)
