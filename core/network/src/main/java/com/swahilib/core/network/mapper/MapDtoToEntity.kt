package com.swahilib.core.network.mapper

import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.database.entities.content.WordEntity
import com.swahilib.core.network.dtos.IdiomDto
import com.swahilib.core.network.dtos.ProverbDto
import com.swahilib.core.network.dtos.SayingDto
import com.swahilib.core.network.dtos.WordDto

object MapDtoToEntity {
    fun mapToEntity(dto: WordDto) = WordEntity(
        rid = dto.rid,
        title = dto.title,
        synonyms = dto.synonyms,
        meaning = dto.meaning,
        conjugation = dto.conjugation,
        english = dto.english,
    )

    fun mapToEntity(dto: IdiomDto) = IdiomEntity(
        rid = dto.rid,
        title = dto.title,
        meaning = dto.meaning,
    )

    fun mapToEntity(dto: ProverbDto) = ProverbEntity(
        rid = dto.rid,
        title = dto.title,
        synonyms = dto.synonyms,
        meaning = dto.meaning,
        conjugation = dto.conjugation,
    )

    fun mapToEntity(dto: SayingDto) = SayingEntity(
        rid = dto.rid,
        title = dto.title,
        meaning = dto.meaning,
    )
}
