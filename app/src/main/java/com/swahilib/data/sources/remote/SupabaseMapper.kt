package com.swahilib.data.sources.remote

import com.swahilib.data.models.Word
import com.swahilib.data.sources.remote.dtos.WordDto
import java.util.Date

object SupabaseMapper {
    fun mapToEntity(word: WordDto): Word {
        return Word(
            rid = word.rid,
            title = word.title,
            synonyms = word.synonyms,
            meaning = word.meaning,
            conjugation = word.conjugation,
            createdAt = word.createdAt,
            id = TODO(),
            updatedAt = TODO(),
        )
    }
}