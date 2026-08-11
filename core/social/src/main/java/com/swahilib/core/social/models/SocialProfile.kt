package com.swahilib.core.social.models

data class SocialProfile(
    val userId: String,
    val displayName: String,
    val avatarKey: String,
    val level: Int,
    val totalXp: Int,
    val currentStreak: Int,
    val friendCode: String,
)
