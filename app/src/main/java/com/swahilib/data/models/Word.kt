package com.swahilib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.swahilib.core.utils.Collections
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Keep
@Parcelize
@Serializable
@Entity(
    tableName = Collections.WORDS,
    indices = [Index(value = ["id"], unique = true)]
)
data class Word(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "rid") val rid: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "synonyms") val synonyms: String,
    @ColumnInfo(name = "meaning") val meaning: String,
    @ColumnInfo(name = "conjugation") val conjugation: String,
    @ColumnInfo(name = "views") val views: Int,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "createdAt") val createdAt: String,
    @ColumnInfo(name = "updatedAt") val updatedAt: String,
) : Parcelable
