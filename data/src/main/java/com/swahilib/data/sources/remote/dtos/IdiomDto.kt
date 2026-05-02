package com.swahilib.data.sources.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class IdiomDto(
    val rid: Int = 0,
    val title: String? = null,
    val meaning: String? = null,
    val views: Int = 0,
    val likes: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
