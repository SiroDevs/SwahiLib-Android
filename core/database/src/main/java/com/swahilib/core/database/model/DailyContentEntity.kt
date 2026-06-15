package com.swahilib.core.database.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "daily_content")
data class DailyContentEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "wordRid") val wordRid: Int,
    @ColumnInfo(name = "wordMeaning") val wordMeaning: String,
    @ColumnInfo(name = "proverbRid") val proverbRid: Int,
    @ColumnInfo(name = "proverbMeaning") val proverbMeaning: String,
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
