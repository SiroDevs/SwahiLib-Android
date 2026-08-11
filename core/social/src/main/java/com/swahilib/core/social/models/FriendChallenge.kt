package com.swahilib.core.social.models

data class FriendChallenge(
    val id: String,
    val opponent: SocialProfile,
    val activityType: String,
    val difficulty: String,
    val seed: Long,
    val myScore: Int?,
    val opponentScore: Int?,
    val status: FriendChallengeStatus,
    val isMine: Boolean,
    val expiresAt: String?,
)
