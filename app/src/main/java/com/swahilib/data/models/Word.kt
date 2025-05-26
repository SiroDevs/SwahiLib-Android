package com.swahilib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "books", indices = [Index(value = ["bookId"], unique = true)])
data class Word(
    @PrimaryKey() val bookId: Int,
    @ColumnInfo(name = "bookNo") val bookNo: Int,
    @ColumnInfo(name = "created") val created: String,
    @ColumnInfo(name = "enabled") val enabled: Boolean,
    @ColumnInfo(name = "position") val position: Int,
    @ColumnInfo(name = "songs") val songs: Int,
    @ColumnInfo(name = "subTitle") val subTitle: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "user") val user: Int
) : Parcelable
