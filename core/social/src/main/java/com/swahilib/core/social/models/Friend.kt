package com.swahilib.core.social.models

data class Friend(
    val friendshipId: String,
    val profile: SocialProfile,
    val status: FriendshipStatus,
    val requestedByMe: Boolean,
)
