package com.swahilib.core.database.entities.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "kid_games",
    indices = [Index(value = ["rid"], unique = true)],
)
data class KidGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rid") val rid: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "meaning") val meaning: String? = null,
    @ColumnInfo(name = "reason") val reason: String? = null,
    @ColumnInfo(name = "orderIndex") val orderIndex: Int = 0,
)
