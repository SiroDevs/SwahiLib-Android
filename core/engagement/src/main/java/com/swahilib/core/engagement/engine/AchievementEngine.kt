package com.swahilib.core.engagement.engine

import com.swahilib.core.database.model.AchievementRecordEntity
import com.swahilib.core.database.model.UserProgressEntity
import com.swahilib.core.engagement.catalog.AchievementCatalog
import com.swahilib.core.engagement.model.Achievement
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Detects when the current [UserProgressEntity] crosses an achievement
 * threshold and inserts a record for each newly-unlocked badge. Idempotent:
 * `achievement_records.achievementId` has a unique index, so repeated calls
 * on the same day won't double-award.
 *
 * Streak-based achievements need the *current* streak counter (owned by
 * PrefsRepo, not the DB), so callers can pass it in explicitly via
 * [checkForUnlocksWithStreak].
 */
@Singleton
class AchievementEngine @Inject constructor(
    private val store: ProgressStore,
) {

    suspend fun checkForUnlocks(progress: UserProgressEntity): List<Achievement> =
        evaluate(progress, streak = 0)

    suspend fun checkForUnlocksWithStreak(streak: Int): List<Achievement> {
        val progress = store.loadOrInitProgress()
        return evaluate(progress, streak = streak)
    }

    suspend fun allUnlocked(): List<Achievement> = store.achievementRecordDao.getAll()
        .mapNotNull { record ->
            AchievementCatalog.byId(record.achievementId)?.copy(unlockedAt = record.unlockedAt)
        }

    /** For UI grids: every catalog entry, marked unlocked if we have a record. */
    suspend fun catalogWithStatus(): List<Achievement> {
        val unlockedById = store.achievementRecordDao.getAll().associateBy { it.achievementId }
        return AchievementCatalog.ALL.map { def ->
            unlockedById[def.id]?.let { def.copy(unlockedAt = it.unlockedAt) } ?: def
        }
    }

    private suspend fun evaluate(
        progress: UserProgressEntity,
        streak: Int,
    ): List<Achievement> {
        val unlocked = mutableListOf<Achievement>()
        var coinDelta = 0
        for (def in AchievementCatalog.ALL) {
            if (!predicate(def.id, progress, streak)) continue
            val record = AchievementRecordEntity(
                achievementId = def.id,
                unlockedAt = store.clock.now(),
                xpAwarded = def.xpReward,
                coinsAwarded = def.coinReward,
            )
            val rowId = store.achievementRecordDao.insert(record)
            if (rowId != -1L) {
                // INSERT-OR-IGNORE returned a new row - this really is a first
                // unlock. Batch the coin credits so simultaneous unlocks don't
                // clobber each other with stale copies of `progress`.
                coinDelta += def.coinReward
                unlocked += def.copy(unlockedAt = record.unlockedAt)
            }
        }
        if (coinDelta > 0) {
            val fresh = store.loadOrInitProgress()
            store.writeProgress(fresh.copy(coins = fresh.coins + coinDelta))
        }
        return unlocked
    }

    private fun predicate(
        id: String,
        progress: UserProgressEntity,
        streak: Int,
    ): Boolean = when (id) {
        AchievementCatalog.FIRST_STEPS -> progress.challengesCompleted >= 1
        AchievementCatalog.WEEK_WARRIOR -> streak >= 7
        AchievementCatalog.MONTH_MASTER -> streak >= 30
        AchievementCatalog.CENTURION -> streak >= 100
        AchievementCatalog.CHALLENGE_ROOKIE -> progress.challengesCompleted >= 5
        AchievementCatalog.CHALLENGE_REGULAR -> progress.challengesCompleted >= 25
        AchievementCatalog.CHALLENGE_LEGEND -> progress.challengesCompleted >= 100
        AchievementCatalog.WORD_COLLECTOR_10 -> progress.wordsLearned >= 10
        AchievementCatalog.WORD_COLLECTOR_50 -> progress.wordsLearned >= 50
        AchievementCatalog.WORD_COLLECTOR_200 -> progress.wordsLearned >= 200
        AchievementCatalog.LEVEL_5 -> progress.level >= 5
        AchievementCatalog.LEVEL_10 -> progress.level >= 10
        AchievementCatalog.LEVEL_25 -> progress.level >= 25
        AchievementCatalog.QUIZ_SHARPSHOOTER -> false // opt-in, awarded explicitly on perfect quiz
        else -> false
    }

    /** Called by the quiz engine when a user gets 100% on a quiz. */
    suspend fun awardExplicit(id: String): Achievement? {
        val def = AchievementCatalog.byId(id) ?: return null
        val record = AchievementRecordEntity(
            achievementId = def.id,
            unlockedAt = store.clock.now(),
            xpAwarded = def.xpReward,
            coinsAwarded = def.coinReward,
        )
        val rowId = store.achievementRecordDao.insert(record)
        if (rowId == -1L) return null
        val progress = store.loadOrInitProgress()
        if (def.coinReward > 0) {
            store.writeProgress(progress.copy(coins = progress.coins + def.coinReward))
        }
        return def.copy(unlockedAt = record.unlockedAt)
    }
}
