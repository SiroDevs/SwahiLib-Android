package com.swahilib.core.database.entities.library

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "countries",
    indices = [Index(value = ["continent"])],
)
data class CountryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "rid") val rid: String,
    @ColumnInfo(name = "continent") val continent: String,
    @ColumnInfo(name = "countries") val countries: String,
    @ColumnInfo(name = "english") val english: String? = null,
    @ColumnInfo(name = "nationality") val nationality: String? = null,
    @ColumnInfo(name = "capital") val capital: String? = null,
    @ColumnInfo(name = "language") val language: String? = null,
    @ColumnInfo(name = "currency") val currency: String? = null,
    @ColumnInfo(name = "currCode") val currCode: String? = null,
    @ColumnInfo(name = "code") val code: String? = null,
    @ColumnInfo(name = "orderIndex") val orderIndex: Int = 0,
)
