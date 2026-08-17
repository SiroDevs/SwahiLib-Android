package com.swahilib.core.database.entities.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "greetings",
    indices = [Index(value = ["rid"], unique = true)],
)
data class GreetingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rid") val rid: String,
    @ColumnInfo(name = "greeting") val greeting: String,
    @ColumnInfo(name = "answer") val answer: String? = null,
    @ColumnInfo(name = "person1") val person1: String? = null,
    @ColumnInfo(name = "person2") val person2: String? = null,
    @ColumnInfo(name = "time") val time: String? = null,
    @ColumnInfo(name = "orderIndex") val orderIndex: Int = 0,
)
