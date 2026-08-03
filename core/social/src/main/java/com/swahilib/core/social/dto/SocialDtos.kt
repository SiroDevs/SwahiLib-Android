package com.swahilib.core.social.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_key") val avatarKey: String = "default",
    val level: Int = 1,
    @SerialName("total_xp") val totalXp: Int = 0,
    @SerialName("current_streak") val currentStreak: Int = 0,
    @SerialName("friend_code") val friendCode: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class FriendshipDto(
    val id: String? = null,
    @SerialName("requester_id") val requesterId: String,
    @SerialName("addressee_id") val addresseeId: String,
    val status: String = "pending", // pending | accepted | blocked
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("responded_at") val respondedAt: String? = null,
)

@Serializable
data class WeeklyCompetitionDto(
    val id: String? = null,
    @SerialName("period_key") val periodKey: String,
    @SerialName("starts_at") val startsAt: String,
    @SerialName("ends_at") val endsAt: String,
)

@Serializable
data class WeeklyScoreDto(
    @SerialName("competition_id") val competitionId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("xp_earned") val xpEarned: Int,
)

@Serializable
data class FriendChallengeDto(
    val id: String? = null,
    @SerialName("challenger_id") val challengerId: String,
    @SerialName("opponent_id") val opponentId: String,
    @SerialName("activity_type") val activityType: String,
    val difficulty: String,
    val seed: Long,
    @SerialName("challenger_score") val challengerScore: Int? = null,
    @SerialName("opponent_score") val opponentScore: Int? = null,
    val status: String = "pending", // pending | active | completed | declined | expired
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
)

@Serializable
data class AchievementFeedDto(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("achievement_id") val achievementId: String,
    @SerialName("unlocked_at") val unlockedAt: String? = null,
)

/** Convenience projection for leaderboard rendering - same shape as ProfileDto minus friend_code. */
data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val avatarKey: String,
    val level: Int,
    val totalXp: Int,
    val rank: Int,
    val isCurrentUser: Boolean,
)
