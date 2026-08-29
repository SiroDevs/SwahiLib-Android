package com.swahilib.core.database.entities.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** `library/insects.json` — insect names, grouped by category. 1 attribute -> grouped grid. */
@Entity(
    tableName = "insects",
    indices = [Index(value = ["category"])],
)
data class InsectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rid") val rid: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "orderIndex") val orderIndex: Int = 0,
)
