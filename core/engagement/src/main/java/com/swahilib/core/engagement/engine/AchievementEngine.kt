package com.swahilib.core.engagement.engine

import com.swahilib.core.database.model.AchievementRecordEntity
import com.swahilib.core.database.model.UserProgressEntity
import com.swahilib.core.engagement.catalog.AchievementCatalog
import com.swahilib.core.engagement.model.Achievement
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementEngine @Inject constructor(
    private val store: ProgressStore,
) {
    companion object {
        private val GAME_TYPES = listOf(
            "QUIZ", "WORD_BUILDER", "SENTENCE_BUILDER", "SPELLING", "CROSSWORD", "SUDOKU", "PROVERB",
        )
    }

    suspend fun checkForUnlocks(progress: UserProgressEntity): List<Achievement> =
        evaluate(progress, streak = 0)

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
        AchievementCatalog.WORD_EXPERT -> (counts.playsByType["WORD_BUILDER"] ?: 0) >= 25
        AchievementCatalog.SENTENCE_MASTER -> (counts.playsByType["SENTENCE_BUILDER"] ?: 0) >= 25
        AchievementCatalog.CROSSWORD_CHAMPION -> (counts.playsByType["CROSSWORD"] ?: 0) >= 10
        AchievementCatalog.SUDOKU_WIZARD -> (counts.playsByType["SUDOKU"] ?: 0) >= 15
        AchievementCatalog.SPELLING_CHAMPION -> (counts.playsByType["SPELLING"] ?: 0) >= 25
        AchievementCatalog.PROVERB_SAGE -> (counts.playsByType["PROVERB"] ?: 0) >= 20
        AchievementCatalog.PERFECT_STREAK_5 -> counts.perfectByType.values.sum() >= 5
        AchievementCatalog.GRAND_SLAM -> GAME_TYPES.all { (counts.perfectByType[it] ?: 0) >= 1 }
        else -> false
    }

    private data class GameCounts(
        val playsByType: Map<String, Int>,
        val perfectByType: Map<String, Int>,
    )

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
