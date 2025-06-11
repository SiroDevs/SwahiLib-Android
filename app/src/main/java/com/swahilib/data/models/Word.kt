package com.swahilib.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.*
import com.swahilib.core.utils.Collections
import com.swahilib.domain.models.WordModel
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = Collections.WORDS, indices = [Index(value = ["id"], unique = true)])
data class Word(
    @PrimaryKey() val id: Int,
    @ColumnInfo(name = "rid") val rid: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "synonyms") val synonyms: String,
    @ColumnInfo(name = "meaning") val meaning: String,
    @ColumnInfo(name = "conjugation") val conjugation: String,
    @ColumnInfo(name = "createdAt") val createdAt: String,
    @ColumnInfo(name = "updatedAt") val updatedAt: String,
) : Parcelable


fun List<Word>.asDomainModel(): List<WordModel> {
    return map {
        it.asDomainModel()
    }
}

fun Word.asDomainModel(): WordModel {
    return WordModel(
        id = this.id,
        rid = this.rid,
        title = this.title,
        synonyms = this.synonyms,
        meaning = this.meaning,
        conjugation = this.conjugation,
        createdAt = this.createdAt
    )
}
