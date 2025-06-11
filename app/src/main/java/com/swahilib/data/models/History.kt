package com.swahilib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.swahilib.core.utils.Collections
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = Collections.HISTORY, indices = [Index(value = ["id"], unique = true)])
data class History(
    @PrimaryKey() val id: Int,
    @ColumnInfo(name = "rid") val rid: Int,
    @ColumnInfo(name = "item") val item: Int,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "createdAt") val createdAt: String,
) : Parcelable
