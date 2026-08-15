package com.swahilib.core.database.entities.content

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
    tableName = "history",
    indices = [Index(value = ["id"], unique = true)]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "item") val item: Int,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "createdAt") val createdAt: String,
) : Parcelable
