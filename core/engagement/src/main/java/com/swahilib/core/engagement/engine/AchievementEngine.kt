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
    companion object {
        /** Every `StatisticsEngine.EventType` that represents an actual mini-game (excludes lookups/reads). */
        private val GAME_TYPES = listOf(
            "QUIZ", "WORD_BUILDER", "SENTENCE_BUILDER", "SPELLING", "CROSSWORD", "WORD_SEARCH", "PROVERB",
        )
    }

    suspend fun checkForUnlocks(progress: UserProgressEntity): List<Achievement> =
        evaluate(progress, streak = 0)

    /** Called after any single game/quiz session completes, so unlocks show up immediately rather than waiting for the next app-open. */
    suspend fun checkForUnlocksAfterActivity(): List<Achievement> {
        val progress = store.loadOrInitProgress()
        return evaluate(progress, streak = 0)
    }

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
        val counts = GameCounts(
            playsByType = GAME_TYPES.associateWith { store.learningHistoryDao.countByType(it) },
            perfectByType = GAME_TYPES.associateWith { store.learningHistoryDao.countPerfectByType(it) },
        )

        val unlocked = mutableListOf<Achievement>()
        var coinDelta = 0
        for (def in AchievementCatalog.ALL) {
            if (!predicate(def.id, progress, streak, counts)) continue
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
        counts: GameCounts,
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
        AchievementCatalog.QUIZ_SHARPSHOOTER -> (counts.perfectByType["QUIZ"] ?: 0) >= 1
        AchievementCatalog.VOCAB_APPRENTICE -> (counts.playsByType["QUIZ"] ?: 0) >= 10
        AchievementCatalog.VOCAB_MASTER -> (counts.playsByType["QUIZ"] ?: 0) >= 50
        AchievementCatalog.WORD_BUILDER_EXPERT -> (counts.playsByType["WORD_BUILDER"] ?: 0) >= 25
        AchievementCatalog.SENTENCE_MASTER -> (counts.playsByType["SENTENCE_BUILDER"] ?: 0) >= 25
        AchievementCatalog.CROSSWORD_CHAMPION -> (counts.playsByType["CROSSWORD"] ?: 0) >= 10
        AchievementCatalog.WORD_SEARCH_WIZARD -> (counts.playsByType["WORD_SEARCH"] ?: 0) >= 15
        AchievementCatalog.SPELLING_BEE_CHAMPION -> (counts.playsByType["SPELLING"] ?: 0) >= 25
        AchievementCatalog.PROVERB_SAGE -> (counts.playsByType["PROVERB"] ?: 0) >= 20
        AchievementCatalog.PERFECT_STREAK_5 -> counts.perfectByType.values.sum() >= 5
        AchievementCatalog.GRAND_SLAM -> GAME_TYPES.all { (counts.perfectByType[it] ?: 0) >= 1 }
        else -> false
    }

    /** Snapshot of per-activity-type play/perfect counts, fetched once per [evaluate] pass. */
    private data class GameCounts(
        val playsByType: Map<String, Int>,
        val perfectByType: Map<String, Int>,
    )

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
