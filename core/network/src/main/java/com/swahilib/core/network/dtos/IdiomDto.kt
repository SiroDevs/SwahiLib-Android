package com.swahilib.core.network.dtos

import kotlinx.serialization.Serializable

@Serializable
data class IdiomDto(
    val rid: Int = 0,
    val title: String? = null,
    val meaning: String? = null,
)
