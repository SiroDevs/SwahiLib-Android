package com.swahilib.data.sources.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProverbDto(
    val rid: Int,
    val title: String,
    val synonyms: String,
    val meaning: String,
    val conjugation: String,
    val views: Int,
    val likes: Int,
    val createdAt: String,
    val updatedAt: String,
)
