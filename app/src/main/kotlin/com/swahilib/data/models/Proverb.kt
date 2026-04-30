package com.swahilib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.swahilib.core.utils.Collections
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(
    tableName = Collections.PROVERBS,
    indices = [Index(value = ["rid"], unique = true)]
)
data class Proverb(
    @PrimaryKey val rid: Int = 0,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "synonyms") val synonyms: String? = null,
    @ColumnInfo(name = "meaning") val meaning: String? = null,
    @ColumnInfo(name = "conjugation") val conjugation: String? = null,
    @ColumnInfo(name = "views") val views: Int = 0,
    @ColumnInfo(name = "likes") val likes: Int = 0,
    @ColumnInfo(name = "liked") val liked: Boolean = false,
    @ColumnInfo(name = "createdAt") val createdAt: String? = null,
    @ColumnInfo(name = "updatedAt") val updatedAt: String? = null,
) : Parcelable
