package com.swahilib.core.social.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendChallengeDto(
    val id: String? = null,
    @SerialName("challenger_id") val challengerId: String,
    @SerialName("opponent_id") val opponentId: String,
    @SerialName("activity_type") val activityType: String,
    val difficulty: String,
    val seed: Long,
    @SerialName("challenger_score") val challengerScore: Int? = null,
    @SerialName("opponent_score") val opponentScore: Int? = null,
    val status: String = "pending", // pending | active | completed | declined | expired
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
)
