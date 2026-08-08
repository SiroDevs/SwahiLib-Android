package com.swahilib.core.social.model

enum class FriendshipStatus { PENDING, ACCEPTED, BLOCKED, UNKNOWN;
    companion object {
        fun from(raw: String): FriendshipStatus = entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

enum class FriendChallengeStatus { PENDING, ACTIVE, COMPLETED, DECLINED, EXPIRED, UNKNOWN;
    companion object {
        fun from(raw: String): FriendChallengeStatus = entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: UNKNOWN
    }
}

data class SocialProfile(
    val userId: String,
    val displayName: String,
    val avatarKey: String,
    val level: Int,
    val totalXp: Int,
    val currentStreak: Int,
    val friendCode: String,
)

data class Friend(
    val friendshipId: String,
    val profile: SocialProfile,
    val status: FriendshipStatus,
    val requestedByMe: Boolean,
)

data class FriendChallenge(
    val id: String,
    val opponent: SocialProfile,
    val activityType: String,
    val difficulty: String,
    val seed: Long,
    val myScore: Int?,
    val opponentScore: Int?,
    val status: FriendChallengeStatus,
    val isMine: Boolean, // true if the current user is the challenger
    val expiresAt: String?,
)

data class AchievementFeedItem(
    val friendDisplayName: String,
    val friendAvatarKey: String,
    val achievementId: String,
    val unlockedAt: String?,
)
