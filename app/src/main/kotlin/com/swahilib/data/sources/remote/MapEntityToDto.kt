package com.swahilib.data.sources.remote

import com.swahilib.data.models.*
import com.swahilib.data.sources.remote.dtos.*

object MapEntityToDto {
    fun mapToDto(dto: IdiomDto): IdiomDto {
        return IdiomDto(
            rid = dto.rid,
            title = dto.title,
            meaning = dto.meaning,
            views = dto.views,
            likes = dto.likes,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
    }

    fun mapToDto(dto: ProverbDto): ProverbDto {
        return ProverbDto(
            rid = dto.rid,
            title = dto.title,
            synonyms = dto.synonyms,
            meaning = dto.meaning,
            conjugation = dto.conjugation,
            views = dto.views,
            likes = dto.likes,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
    }

    fun mapToDto(dto: SayingDto): SayingDto {
        return SayingDto(
            rid = dto.rid,
            title = dto.title,
            meaning = dto.meaning,
            views = dto.views,
            likes = dto.likes,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
    }

    fun mapToDto(dto: Word): WordDto {
        return WordDto(
            rid = dto.rid,
            title = dto.title,
            synonyms = dto.synonyms,
            meaning = dto.meaning,
            conjugation = dto.conjugation,
            views = dto.views,
            likes = dto.likes,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
        )
    }
}