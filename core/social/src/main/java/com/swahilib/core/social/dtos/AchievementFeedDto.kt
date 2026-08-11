package com.swahilib.core.social.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AchievementFeedDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("achievement_id") val achievementId: String,
    @SerialName("unlocked_at") val unlockedAt: String? = null,
)
