package com.swahilib.core.engagement.model

data class AwardResult(
    val progress: UserProgress,
    val leveledUp: Boolean,
    val previousLevel: Int,
    val unlockedAchievements: List<Achievement>,
    val coinsAwarded: Int,
)
