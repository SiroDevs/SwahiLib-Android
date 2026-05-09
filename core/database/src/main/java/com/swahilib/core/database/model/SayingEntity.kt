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
    tableName = "sayings",
    indices = [Index(value = ["rid"], unique = true)]
)
data class SayingEntity(
    @PrimaryKey val rid: Int = 0,
    @ColumnInfo(name = "title") val title: String? = null,
    @ColumnInfo(name = "meaning") val meaning: String? = null,
    @ColumnInfo(name = "views") val views: Int = 0,
    @ColumnInfo(name = "likes") val likes: Int = 0,
    @ColumnInfo(name = "liked") val liked: Boolean = false,
    @ColumnInfo(name = "createdAt") val createdAt: String? = null,
    @ColumnInfo(name = "updatedAt") val updatedAt: String? = null,
) : Parcelable
