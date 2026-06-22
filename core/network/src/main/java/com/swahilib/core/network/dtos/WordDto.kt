package com.swahilib.core.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class WordDto(
    val rid: Int = 0,
    val title: String? = null,
    val synonyms: String? = null,
    val meaning: String? = null,
    val conjugation: String? = null,
    val english: String? = null,
)
