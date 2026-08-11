package com.swahilib.core.engagement.model

data class ChallengeActivity(
    val id: String,
    val type: ActivityType,
    val title: String,
    val estimatedSeconds: Int,
    val xpReward: Int,
    val completed: Boolean = false,
)

data class Challenge(
    val id: String,
    val scope: ChallengeScope,
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val xpReward: Int,
    val coinReward: Int,
    val activities: List<ChallengeActivity>,
    val createdAt: Long,
    val expiresAt: Long,
    val completed: Boolean = false,
    val completedAt: Long? = null,
) {
    val estimatedSeconds: Int get() = activities.sumOf { it.estimatedSeconds }
    val progress: Float get() =
        if (activities.isEmpty()) 0f
        else activities.count { it.completed }.toFloat() / activities.size
}
