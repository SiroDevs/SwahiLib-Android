package com.swahilib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "songs", indices = [Index(value = ["songId"], unique = true)])
data class Song(
    @ColumnInfo(name = "alias") val alias: String,
    @ColumnInfo(name = "book") val book: Int,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "created") val created: String,
    @ColumnInfo(name = "liked") val liked: Boolean,
    @ColumnInfo(name = "likes") val likes: Int,
    @PrimaryKey() val songId: Int,
    @ColumnInfo(name = "songNo") val songNo: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "views") val views: Int
): Parcelable
