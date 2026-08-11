package com.swahilib.core.social.dtos

data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val avatarKey: String,
    val level: Int,
    val totalXp: Int,
    val rank: Int,
    val isCurrentUser: Boolean,
)
