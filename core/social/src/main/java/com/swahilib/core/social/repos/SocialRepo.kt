package com.swahilib.core.social.repos

import com.swahilib.core.social.dto.AchievementFeedDto
import com.swahilib.core.social.dto.FriendChallengeDto
import com.swahilib.core.social.dto.FriendshipDto
import com.swahilib.core.social.dto.LeaderboardEntry
import com.swahilib.core.social.dto.ProfileDto
import com.swahilib.core.social.model.AchievementFeedItem
import com.swahilib.core.social.model.Friend
import com.swahilib.core.social.model.FriendChallenge
import com.swahilib.core.social.model.FriendChallengeStatus
import com.swahilib.core.social.model.FriendshipStatus
import com.swahilib.core.social.model.SocialProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlin.random.Random
import javax.inject.Inject

class SocialRepo @Inject constructor(
    private val supabase: SupabaseClient,
    private val authRepo: SocialAuthRepo,
) {
    private val userId: String? get() = authRepo.currentUserId

    suspend fun ensureProfile(displayName: String): SocialProfile? {
        val uid = userId ?: return null
        val existing = supabase.from("profiles").select {
            filter { eq("id", uid) }
        }.decodeSingleOrNull<ProfileDto>()
        if (existing != null) return existing.toDomain()

        var attempt = 0
        var created: ProfileDto? = null
        while (created == null && attempt < 5) {
            val code = generateFriendCode()
            created = runCatching {
                supabase.from("profiles").insert(
                    ProfileDto(id = uid, displayName = displayName, friendCode = code),
                ) { select() }.decodeSingle<ProfileDto>()
            }.getOrNull()
            attempt++
        }
        return created?.toDomain()
    }

    suspend fun currentProfile(): SocialProfile? {
        val uid = userId ?: return null
        return supabase.from("profiles").select {
            filter { eq("id", uid) }
        }.decodeSingleOrNull<ProfileDto>()?.toDomain()
    }

    /** Pushes the latest local snapshot (from ProgressStore) up to Supabase. Called by SocialSyncWorker. */
    suspend fun syncProgress(level: Int, totalXp: Int, currentStreak: Int) {
        val uid = userId ?: return
        supabase.from("profiles").update(
            mapOf("level" to level, "total_xp" to totalXp, "current_streak" to currentStreak),
        ) {
            filter { eq("id", uid) }
        }
    }

    // ── Leaderboard ─────────────────────────────────────────────────────

    suspend fun globalLeaderboard(limit: Int = 50): List<LeaderboardEntry> {
        val uid = userId
        val profiles = supabase.from("profiles").select(
            columns = Columns.list("id", "display_name", "avatar_key", "level", "total_xp"),
        ) {
            order("total_xp", order = Order.DESCENDING)
            limit(limit.toLong())
        }.decodeList<ProfileDto>()

        return profiles.mapIndexed { index, dto ->
            LeaderboardEntry(
                userId = dto.id,
                displayName = dto.displayName,
                avatarKey = dto.avatarKey,
                level = dto.level,
                totalXp = dto.totalXp,
                rank = index + 1,
                isCurrentUser = dto.id == uid,
            )
        }
    }

    suspend fun friendsLeaderboard(): List<LeaderboardEntry> {
        val friendIds = friends().filter { it.status == FriendshipStatus.ACCEPTED }.map { it.profile.userId }
        val uid = userId
        val allIds = (friendIds + listOfNotNull(uid)).distinct()
        if (allIds.isEmpty()) return emptyList()

        val profiles = supabase.from("profiles").select {
            filter { isIn("id", allIds) }
            order("total_xp", order = Order.DESCENDING)
        }.decodeList<ProfileDto>()

        return profiles.mapIndexed { index, dto ->
            LeaderboardEntry(
                userId = dto.id,
                displayName = dto.displayName,
                avatarKey = dto.avatarKey,
                level = dto.level,
                totalXp = dto.totalXp,
                rank = index + 1,
                isCurrentUser = dto.id == uid,
            )
        }
    }

    // ── Friends ─────────────────────────────────────────────────────────

    suspend fun friends(): List<Friend> {
        val uid = userId ?: return emptyList()
        val friendships = supabase.from("friendships").select {
            filter {
                or {
                    eq("requester_id", uid)
                    eq("addressee_id", uid)
                }
            }
        }.decodeList<FriendshipDto>()
        if (friendships.isEmpty()) return emptyList()

        val otherIds = friendships.map { if (it.requesterId == uid) it.addresseeId else it.requesterId }
        val profiles = supabase.from("profiles").select {
            filter { isIn("id", otherIds) }
        }.decodeList<ProfileDto>().associateBy { it.id }

        return friendships.mapNotNull { f ->
            val otherId = if (f.requesterId == uid) f.addresseeId else f.requesterId
            val profile = profiles[otherId] ?: return@mapNotNull null
            Friend(
                friendshipId = f.id.orEmpty(),
                profile = profile.toDomain(),
                status = FriendshipStatus.from(f.status),
                requestedByMe = f.requesterId == uid,
            )
        }
    }

    /** Looks up a user by their shareable friend code and sends a request. */
    suspend fun sendFriendRequest(friendCode: String): Result<Unit> = runCatching {
        val uid = userId ?: error("Not signed in")
        val target = supabase.from("profiles").select {
            filter { eq("friend_code", friendCode.trim().uppercase()) }
        }.decodeSingleOrNull<ProfileDto>() ?: error("Hakuna mtumiaji mwenye msimbo huo")

        if (target.id == uid) error("Huwezi kujiongeza mwenyewe")

        supabase.from("friendships").insert(
            FriendshipDto(requesterId = uid, addresseeId = target.id, status = "pending"),
        )
    }

    suspend fun respondToFriendRequest(friendshipId: String, accept: Boolean): Result<Unit> = runCatching {
        supabase.from("friendships").update(
            mapOf("status" to if (accept) "accepted" else "blocked"),
        ) {
            filter { eq("id", friendshipId) }
        }
    }

    // ── Friend Challenges ───────────────────────────────────────────────

    suspend fun createFriendChallenge(opponentId: String, activityType: String, difficulty: String): Result<FriendChallenge> = runCatching {
        val uid = userId ?: error("Not signed in")
        val seed = Random.nextLong()
        val dto = supabase.from("challenges").insert(
            FriendChallengeDto(
                challengerId = uid,
                opponentId = opponentId,
                activityType = activityType,
                difficulty = difficulty,
                seed = seed,
                status = "pending",
            ),
        ) { select() }.decodeSingle<FriendChallengeDto>()

        val opponentProfile = supabase.from("profiles").select {
            filter { eq("id", opponentId) }
        }.decodeSingle<ProfileDto>()

        dto.toDomain(currentUserId = uid, opponentProfile = opponentProfile.toDomain())
    }

    suspend fun myFriendChallenges(): List<FriendChallenge> {
        val uid = userId ?: return emptyList()
        val challenges = supabase.from("challenges").select {
            filter {
                or {
                    eq("challenger_id", uid)
                    eq("opponent_id", uid)
                }
            }
        }.decodeList<FriendChallengeDto>()
        if (challenges.isEmpty()) return emptyList()

        val opponentIds = challenges.map { if (it.challengerId == uid) it.opponentId else it.challengerId }.distinct()
        val profiles = supabase.from("profiles").select {
            filter { isIn("id", opponentIds) }
        }.decodeList<ProfileDto>().associateBy { it.id }

        return challenges.mapNotNull { c ->
            val opponentId = if (c.challengerId == uid) c.opponentId else c.challengerId
            val opponent = profiles[opponentId] ?: return@mapNotNull null
            c.toDomain(currentUserId = uid, opponentProfile = opponent.toDomain())
        }
    }

    suspend fun submitFriendChallengeScore(challengeId: String, score: Int, iAmChallenger: Boolean): Result<Unit> = runCatching {
        val current = supabase.from("challenges").select {
            filter { eq("id", challengeId) }
        }.decodeSingle<FriendChallengeDto>()

        val otherScoreAlreadyIn = if (iAmChallenger) current.opponentScore != null else current.challengerScore != null
        val field = if (iAmChallenger) "challenger_score" else "opponent_score"
        val newStatus = if (otherScoreAlreadyIn) "completed" else "active"

        supabase.from("challenges").update(mapOf(field to score, "status" to newStatus)) {
            filter { eq("id", challengeId) }
        }
    }

    suspend fun declineFriendChallenge(challengeId: String): Result<Unit> = runCatching {
        supabase.from("challenges").update(mapOf("status" to "declined")) {
            filter { eq("id", challengeId) }
        }
    }

    // ── Achievement Feed ────────────────────────────────────────────────

    suspend fun postAchievementUnlock(achievementId: String): Result<Unit> = runCatching {
        val uid = userId ?: error("Not signed in")
        supabase.from("achievements").insert(
            AchievementFeedDto(userId = uid, achievementId = achievementId),
        )
    }

    suspend fun friendsAchievementFeed(limit: Int = 30): List<AchievementFeedItem> {
        val friendIds = friends().filter { it.status == FriendshipStatus.ACCEPTED }.map { it.profile.userId }
        if (friendIds.isEmpty()) return emptyList()

        val items = supabase.from("achievements").select {
            filter { isIn("user_id", friendIds) }
            order("unlocked_at", order = Order.DESCENDING)
            limit(limit.toLong())
        }.decodeList<AchievementFeedDto>()

        val profiles = supabase.from("profiles").select {
            filter { isIn("id", items.map { it.userId }.distinct()) }
        }.decodeList<ProfileDto>().associateBy { it.id }

        return items.mapNotNull { item ->
            val profile = profiles[item.userId] ?: return@mapNotNull null
            AchievementFeedItem(
                friendDisplayName = profile.displayName,
                friendAvatarKey = profile.avatarKey,
                achievementId = item.achievementId,
                unlockedAt = item.unlockedAt,
            )
        }
    }

    private fun generateFriendCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // no O/0/I/1 to avoid ambiguity
        return (1..6).map { chars.random() }.joinToString("")
    }

    private fun ProfileDto.toDomain() = SocialProfile(
        userId = id,
        displayName = displayName,
        avatarKey = avatarKey,
        level = level,
        totalXp = totalXp,
        currentStreak = currentStreak,
        friendCode = friendCode,
    )

    private fun FriendChallengeDto.toDomain(currentUserId: String, opponentProfile: SocialProfile) = FriendChallenge(
        id = id.orEmpty(),
        opponent = opponentProfile,
        activityType = activityType,
        difficulty = difficulty,
        seed = seed,
        myScore = if (challengerId == currentUserId) challengerScore else opponentScore,
        opponentScore = if (challengerId == currentUserId) opponentScore else challengerScore,
        status = FriendChallengeStatus.from(status),
        isMine = challengerId == currentUserId,
        expiresAt = expiresAt,
    )
}
