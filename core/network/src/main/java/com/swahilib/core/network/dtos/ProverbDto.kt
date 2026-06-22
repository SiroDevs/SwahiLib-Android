package com.swahilib.core.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProverbDto(
    val rid: Int = 0,
    val title: String? = null,
    val synonyms: String? = null,
    val meaning: String? = null,
    val conjugation: String? = null,
)
