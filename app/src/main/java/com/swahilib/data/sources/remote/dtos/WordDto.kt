package com.swahilib.data.sources.remote.dtos

import kotlinx.serialization.*

@Serializable
data class WordDto(
    @SerialName("rid")
    val rid: Int,
    @SerialName("title")
    val title: String,
    @SerialName("synonyms")
    val synonyms: String,
    @SerialName("meaning")
    val meaning: String,
    @SerialName("conjugation")
    val conjugation: String,
    @SerialName("createdAt")
    val createdAt: Int,
)
