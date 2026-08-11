package com.swahilib.core.social.models

data class AchievementFeedItem(
    val friendDisplayName: String,
    val friendAvatarKey: String,
    val achievementId: String,
    val unlockedAt: String?,
)
