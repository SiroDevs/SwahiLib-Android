package com.swahilib.core.database.entities.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "punctuation",
    indices = [Index(value = ["rid"], unique = true)],
)
data class PunctuationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rid") val rid: String,
    @ColumnInfo(name = "sign") val sign: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "orderIndex") val orderIndex: Int = 0,
)
