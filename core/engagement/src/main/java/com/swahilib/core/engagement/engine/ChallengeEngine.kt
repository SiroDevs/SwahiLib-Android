package com.swahilib.core.engagement.engine

import com.swahilib.core.database.model.ChallengeActivityEntity
import com.swahilib.core.database.model.ChallengeEntity
import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.Challenge
import com.swahilib.core.engagement.model.ChallengeActivity
import com.swahilib.core.engagement.model.ChallengeScope
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.engagement.time.TimeKeys
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates and mutates challenges. Idempotent per (scope, periodKey), so
 * calling `ensureDailyChallenge()` on every app open is safe. Completion is
 * handled through [markActivityComplete] which will auto-fire the challenge
 * completion reward once every activity is done.
 */
@Singleton
class ChallengeEngine @Inject constructor(
    private val store: ProgressStore,
    private val xpEngine: XpEngine,
    private val rewardsEngine: RewardsEngine,
) {

    suspend fun ensureDailyChallenge(difficulty: Difficulty = Difficulty.BEGINNER): Challenge {
        val periodKey = TimeKeys.today(store.clock)
        val template = ChallengeTemplates.daily(periodKey, difficulty)
        return upsertFromTemplate(
            periodKey = periodKey,
            template = template,
            expiresAt = TimeKeys.endOfDay(store.clock),
        )
    }

    suspend fun ensureWeeklyChallenge(): Challenge {
        val periodKey = TimeKeys.weekKey(store.clock)
        val template = ChallengeTemplates.weekly(periodKey)
        return upsertFromTemplate(
            periodKey = periodKey,
            template = template,
            expiresAt = TimeKeys.endOfWeek(store.clock),
        )
    }

    suspend fun ensureMonthlyChallenge(): Challenge {
        val periodKey = TimeKeys.monthKey(store.clock)
        val template = ChallengeTemplates.monthly(periodKey)
        return upsertFromTemplate(
            periodKey = periodKey,
            template = template,
            expiresAt = TimeKeys.endOfMonth(store.clock),
        )
    }

    suspend fun createPracticeSession(difficulty: Difficulty = Difficulty.BEGINNER): Challenge {
        val template = ChallengeTemplates.practice(difficulty)
        val id = "practice_${store.clock.now()}"
        val entity = ChallengeEntity(
            id = id,
            scope = template.scope.name,
            periodKey = id,
            title = template.title,
            description = template.description,
            difficulty = template.difficulty.name,
            xpReward = 0,
            coinReward = 0,
            createdAt = store.clock.now(),
            expiresAt = store.clock.now() + PRACTICE_TTL_MS,
        )
        val activities = template.activities.mapIndexed { index, activity ->
            activity.toEntity(challengeId = id, orderIndex = index)
        }
        store.challengeDao.insertWithActivities(entity, activities)
        return toDomain(entity, activities)
    }

    suspend fun getById(id: String): Challenge? {
        val entity = store.challengeDao.getById(id) ?: return null
        val activities = store.challengeDao.getActivitiesFor(id)
        return toDomain(entity, activities)
    }

    /**
     * Marks a single activity in a challenge as complete, awards XP for it,
     * and - if every activity in the challenge is now done - fires the
     * challenge-completion bonus exactly once.
     *
     * Returns null if the challenge or activity can't be found, or if the
     * activity was already completed (idempotent second-call safety).
     */
    suspend fun markActivityComplete(
        challengeId: String,
        activityId: String,
        secondsSpent: Int = 0,
    ): CompletionResult? {
        val challenge = store.challengeDao.getById(challengeId) ?: return null
        val activities = store.challengeDao.getActivitiesFor(challengeId)
        val activity = activities.firstOrNull { it.id == activityId } ?: return null
        if (activity.completed) return null

        val now = store.clock.now()
        val updatedActivity = activity.copy(completed = true, completedAt = now)
        store.challengeDao.updateActivity(updatedActivity)

        val difficulty = runCatching { Difficulty.valueOf(challenge.difficulty) }
            .getOrDefault(Difficulty.BEGINNER)
        val activityAward = xpEngine.award(
            XpAward(
                source = XpSource.ACTIVITY_COMPLETE,
                amount = activity.xpReward,
                referenceId = activity.id,
                activityType = runCatching { ActivityType.valueOf(activity.type) }.getOrNull(),
                secondsSpent = secondsSpent,
            )
        )
        store.mutateDay { day -> day.copy(activitiesCompleted = day.activitiesCompleted + 1) }

        val nextActivities = activities.map { if (it.id == activityId) updatedActivity else it }
        val allDone = nextActivities.all { it.completed }

        var challengeCompletion: AwardResult? = null
        if (allDone && !challenge.completed) {
            val activityXpTotal = activities.sumOf { it.xpReward }
            val bonusXp = RewardRules.challengeCompletionXp(
                scope = ChallengeScope.valueOf(challenge.scope),
                activityXpTotal = activityXpTotal,
            )
            val bonusCoins = RewardRules.challengeCompletionCoins(ChallengeScope.valueOf(challenge.scope))
            if (bonusCoins > 0) rewardsEngine.grantCoins(bonusCoins)
            val bonusAward = xpEngine.award(
                XpAward(
                    source = XpSource.CHALLENGE_COMPLETE,
                    amount = bonusXp,
                    referenceId = challenge.id,
                )
            ).copy(coinsAwarded = bonusCoins)

            store.challengeDao.updateChallenge(
                challenge.copy(completed = true, completedAt = now)
            )
            val progress = store.loadOrInitProgress()
            store.writeProgress(progress.copy(challengesCompleted = progress.challengesCompleted + 1))
            store.mutateDay { day -> day.copy(challengesCompleted = day.challengesCompleted + 1) }
            challengeCompletion = bonusAward
        }

        return CompletionResult(
            challenge = toDomain(challenge, nextActivities),
            activityAward = activityAward,
            challengeCompletionAward = challengeCompletion,
        )
    }

    suspend fun activeChallenges(): List<Challenge> {
        val now = store.clock.now()
        val dailyKey = TimeKeys.today(store.clock)
        val weeklyKey = TimeKeys.weekKey(store.clock)
        val monthlyKey = TimeKeys.monthKey(store.clock)
        return listOfNotNull(
            store.challengeDao.getByPeriod(ChallengeScope.DAILY.name, dailyKey),
            store.challengeDao.getByPeriod(ChallengeScope.WEEKLY.name, weeklyKey),
            store.challengeDao.getByPeriod(ChallengeScope.MONTHLY.name, monthlyKey),
        )
            .filter { it.expiresAt > now }
            .map { entity ->
                toDomain(entity, store.challengeDao.getActivitiesFor(entity.id))
            }
    }

    private suspend fun upsertFromTemplate(
        periodKey: String,
        template: ChallengeTemplate,
        expiresAt: Long,
    ): Challenge {
        val existing = store.challengeDao.getByPeriod(template.scope.name, periodKey)
        if (existing != null) {
            val activities = store.challengeDao.getActivitiesFor(existing.id)
            return toDomain(existing, activities)
        }

        val id = "${template.scope.name.lowercase()}_$periodKey"
        val xpReward = RewardRules.challengeCompletionXp(template.scope, template.activityXp)
        val coinReward = RewardRules.challengeCompletionCoins(template.scope)

        val entity = ChallengeEntity(
            id = id,
            scope = template.scope.name,
            periodKey = periodKey,
            title = template.title,
            description = template.description,
            difficulty = template.difficulty.name,
            xpReward = xpReward,
            coinReward = coinReward,
            createdAt = store.clock.now(),
            expiresAt = expiresAt,
        )
        val activities = template.activities.mapIndexed { index, activity ->
            activity.toEntity(challengeId = id, orderIndex = index)
        }
        store.challengeDao.insertWithActivities(entity, activities)
        return toDomain(entity, activities)
    }

    private fun toDomain(
        entity: ChallengeEntity,
        activities: List<ChallengeActivityEntity>,
    ): Challenge = Challenge(
        id = entity.id,
        scope = runCatching { ChallengeScope.valueOf(entity.scope) }.getOrDefault(ChallengeScope.DAILY),
        title = entity.title,
        description = entity.description,
        difficulty = runCatching { Difficulty.valueOf(entity.difficulty) }.getOrDefault(Difficulty.BEGINNER),
        xpReward = entity.xpReward,
        coinReward = entity.coinReward,
        activities = activities
            .sortedBy { it.orderIndex }
            .map { it.toDomain() },
        createdAt = entity.createdAt,
        expiresAt = entity.expiresAt,
        completed = entity.completed,
        completedAt = entity.completedAt,
    )

    data class CompletionResult(
        val challenge: Challenge,
        val activityAward: AwardResult,
        val challengeCompletionAward: AwardResult?,
    )

    companion object {
        private const val PRACTICE_TTL_MS = 24L * 60 * 60 * 1000
    }
}

private fun ChallengeActivity.toEntity(challengeId: String, orderIndex: Int): ChallengeActivityEntity =
    ChallengeActivityEntity(
        id = id,
        challengeId = challengeId,
        orderIndex = orderIndex,
        type = type.name,
        title = title,
        estimatedSeconds = estimatedSeconds,
        xpReward = xpReward,
        completed = completed,
    )

private fun ChallengeActivityEntity.toDomain(): ChallengeActivity = ChallengeActivity(
    id = id,
    type = runCatching { ActivityType.valueOf(type) }.getOrDefault(ActivityType.CUSTOM),
    title = title,
    estimatedSeconds = estimatedSeconds,
    xpReward = xpReward,
    completed = completed,
)
