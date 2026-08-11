package com.swahilib.core.social.models

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
