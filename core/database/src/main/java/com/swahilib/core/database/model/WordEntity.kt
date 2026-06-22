package com.swahilib.core.database.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(
    tableName = "words",
    indices = [Index(value = ["rid"], unique = true)]
)
data class WordEntity(
    @PrimaryKey val rid: Int = 0,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "english") val english: String? = null,
    @ColumnInfo(name = "synonyms") val synonyms: String? = null,
    @ColumnInfo(name = "meaning") val meaning: String? = null,
    @ColumnInfo(name = "conjugation") val conjugation: String? = null,
    @ColumnInfo(name = "liked") val liked: Boolean = false,
) : Parcelable
