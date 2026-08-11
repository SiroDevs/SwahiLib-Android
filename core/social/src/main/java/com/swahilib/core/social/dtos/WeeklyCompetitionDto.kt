package com.swahilib.core.social.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeeklyCompetitionDto(
    val id: String? = null,
    @SerialName("period_key") val periodKey: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
)
