package com.swahilib.core.social.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeeklyScoreDto(
    @SerialName("competition_id") val competitionId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("xp_earned") val xpEarned: Int,
)
