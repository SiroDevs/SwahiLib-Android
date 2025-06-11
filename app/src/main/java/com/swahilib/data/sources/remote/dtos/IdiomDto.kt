package com.swahilib.data.sources.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class IdiomDto(
    val rid: Int,
    val title: String,
    val meaning: String,
    val views: Int,
    val likes: Int,
    val createdAt: String,
    val updatedAt: String,
)
