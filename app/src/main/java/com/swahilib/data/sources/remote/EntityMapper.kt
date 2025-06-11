package com.swahilib.data.sources.remote

import com.swahilib.data.models.*
import com.swahilib.data.sources.remote.dtos.*

object EntityMapper {
    fun mapToEntity(entity: IdiomDto): Idiom {
        return Idiom(
            id = entity.rid,
            rid = entity.rid,
            title = entity.title,
            meaning = entity.meaning,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: ProverbDto): Proverb {
        return Proverb(
            id = entity.rid,
            rid = entity.rid,
            title = entity.title,
            synonyms = entity.synonyms,
            meaning = entity.meaning,
            conjugation = entity.conjugation,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: SayingDto): Saying {
        return Saying(
            id = entity.rid,
            rid = entity.rid,
            title = entity.title,
            meaning = entity.meaning,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: WordDto): Word {
        return Word(
            id = entity.rid,
            rid = entity.rid,
            title = entity.title,
            synonyms = entity.synonyms,
            meaning = entity.meaning,
            conjugation = entity.conjugation,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}