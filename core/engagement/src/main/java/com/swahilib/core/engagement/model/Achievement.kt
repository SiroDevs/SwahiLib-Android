package com.swahilib.core.engagement.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val xpReward: Int,
    val coinReward: Int,
    val unlockedAt: Long? = null,
) {
    val unlocked: Boolean get() = unlockedAt != null
}
