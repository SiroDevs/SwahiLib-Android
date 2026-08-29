package com.swahilib.core.database.entities.library

import androidx.room.Embedded
import androidx.room.Relation

data class PunctuationWithUsage(
    @Embedded val punctuation: PunctuationEntity,
    @Relation(parentColumn = "id", entityColumn = "punctuationId")
    val usage: List<PunctuationUsageEntity>,
)
