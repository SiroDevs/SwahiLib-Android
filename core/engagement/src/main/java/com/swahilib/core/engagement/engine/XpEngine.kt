package com.swahilib.core.engagement.engine

import com.swahilib.core.database.model.XpEventEntity
import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.UserProgress
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.time.TimeKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XpEngine @Inject constructor(
    private val store: ProgressStore,
    private val achievementEngine: AchievementEngine,
) {

    suspend fun award(award: XpAward): AwardResult {
        val progress = store.loadOrInitProgress()

        val adjustedAmount = (award.amount * RewardRules.sourceMultiplier(award.source))
            .toInt()
            .coerceAtLeast(0)

        val today = TimeKeys.today(store.clock)
        val now = store.clock.now()

        store.xpEventDao.insert(
            XpEventEntity(
                amount = adjustedAmount,
                source = award.source.name,
                activityType = award.activityType?.name,
                referenceId = award.referenceId,
                date = today,
                createdAt = now,
                secondsSpent = award.secondsSpent,
            )
        )

        val previousLevel = progress.level
        val newTotal = progress.totalXp + adjustedAmount
        val newLevel = LevelCurve.levelForXp(newTotal)

        val updated = progress.copy(
            totalXp = newTotal,
            level = newLevel,
            totalLearningSeconds = progress.totalLearningSeconds + award.secondsSpent,
        )
        store.writeProgress(updated)

        // Roll today's XP counter forward. Learning-seconds are only counted
        // once per event (here) so the daily row and the totals stay in sync.
        store.mutateDay(today) { day ->
            day.copy(
                xpEarned = day.xpEarned + adjustedAmount,
                secondsSpent = day.secondsSpent + award.secondsSpent,
                visited = true,
            )
        }

        // Level-up may itself unlock achievements (e.g. LEVEL_5), so check
        // achievements *after* progress is written.
        val unlocked = achievementEngine.checkForUnlocks(updated)
        val leveledUp = newLevel > previousLevel

        return AwardResult(
            progress = toDomain(updated),
            leveledUp = leveledUp,
            previousLevel = previousLevel,
            unlockedAchievements = unlocked,
            coinsAwarded = 0,
        )
    }

    suspend fun currentProgress(): UserProgress {
        val entity = store.loadOrInitProgress()
        return toDomain(entity)
    }

    private fun toDomain(entity: com.swahilib.core.database.model.UserProgressEntity): UserProgress {
        val (into, span) = LevelCurve.progressWithin(entity.totalXp)
        return UserProgress(
            totalXp = entity.totalXp,
            coins = entity.coins,
            level = entity.level,
            xpIntoLevel = into,
            xpForNextLevel = span,
            currentStreak = 0,
            bestStreak = 0,
            challengesCompleted = entity.challengesCompleted,
            activitiesCompleted = entity.activitiesCompleted,
        )
    }
}
