package com.swahilib.core.engagement.model

data class UserProgress(
    val totalXp: Long,
    val coins: Long,
    val level: Int,
    val xpIntoLevel: Long,
    val xpForNextLevel: Long,
    val currentStreak: Int,
    val bestStreak: Int,
    val challengesCompleted: Int,
    val activitiesCompleted: Int,
) {
    val progressToNextLevel: Float get() =
        if (xpForNextLevel == 0L) 0f
        else (xpIntoLevel.toFloat() / xpForNextLevel.toFloat()).coerceIn(0f, 1f)
}