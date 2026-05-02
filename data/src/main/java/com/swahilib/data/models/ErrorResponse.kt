package com.swahilib.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val status: String,
)
