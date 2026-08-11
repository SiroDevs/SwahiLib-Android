package com.swahilib.core.social.dtos

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
    @SerialName("friend_code") val friendCode: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)
