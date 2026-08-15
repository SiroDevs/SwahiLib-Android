package com.swahilib.core.database.entities.library

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
    tableName = "library_items",
    indices = [
        Index(value = ["collection"]),
        Index(value = ["collection", "groupName"]),
    ]
)
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "collection") val collection: String,
    @ColumnInfo(name = "groupName") val groupName: String? = null,
    @ColumnInfo(name = "rid") val rid: String,
    @ColumnInfo(name = "primaryText") val primaryText: String,
    @ColumnInfo(name = "secondaryText") val secondaryText: String? = null,
    @ColumnInfo(name = "detailsJson") val detailsJson: String = "[]",
    @ColumnInfo(name = "orderIndex") val orderIndex: Int = 0,
) : Parcelable, Parcelable
