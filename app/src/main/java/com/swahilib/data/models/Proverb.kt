package com.swahilib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.swahilib.core.utils.AppConstants
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = AppConstants.PROVERBS, indices = [Index(value = ["id"], unique = true)])
data class Proverb(
    @PrimaryKey() val id: Int,
    @ColumnInfo(name = "rid") val rid: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "synonyms") val synonyms: String,
    @ColumnInfo(name = "meaning") val meaning: String,
    @ColumnInfo(name = "views") val views: Int,
    @ColumnInfo(name = "liked") val liked: Boolean,
    @ColumnInfo(name = "likes") val likes: Int,
    @ColumnInfo(name = "songs") val songs: Int,
    @ColumnInfo(name = "createdAt") val createdAt: String,
    @ColumnInfo(name = "updatedAt") val updatedAt: String,
) : Parcelable
