package com.swahilib.core.network.mapper

import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.network.dtos.IdiomDto
import com.swahilib.core.network.dtos.ProverbDto
import com.swahilib.core.network.dtos.SayingDto
import com.swahilib.core.network.dtos.WordDto

object MapDtoToEntity {
    fun mapToEntity(entity: IdiomDto): IdiomEntity {
        return IdiomEntity(
            rid = entity.rid,
            title = entity.title,
            meaning = entity.meaning,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: ProverbDto): ProverbEntity {
        return ProverbEntity(
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

    fun mapToEntity(entity: SayingDto): SayingEntity {
        return SayingEntity(
            rid = entity.rid,
            title = entity.title,
            meaning = entity.meaning,
            views = entity.views,
            likes = entity.likes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    fun mapToEntity(entity: WordDto): WordEntity {
        return WordEntity(
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