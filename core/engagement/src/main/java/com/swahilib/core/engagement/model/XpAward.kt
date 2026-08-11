package com.swahilib.core.engagement.model

data class XpAward(
    val source: XpSource,
    val amount: Int,
    val referenceId: String? = null,
    val activityType: ActivityType? = null,
    val secondsSpent: Int = 0,
)
