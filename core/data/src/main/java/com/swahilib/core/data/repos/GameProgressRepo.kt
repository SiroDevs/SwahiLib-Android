package com.swahilib.core.data.repos

import com.swahilib.core.database.daos.GameProgressDao
import com.swahilib.core.database.model.GameLevelProgressEntity
import com.swahilib.core.database.model.GameSessionStateEntity
import com.swahilib.core.games.engine.GameLevelConfig
import javax.inject.Inject

/**
 * Per-game "kiwango" (level) unlock/points bookkeeping, plus autosave of the
 * single in-flight session per game so every game (and Quiz) can silently
 * autoresume where the player left off.
 */
class GameProgressRepo @Inject constructor(
    private val dao: GameProgressDao,
) {
    suspend fun getProgress(gameType: String): GameLevelProgressEntity =
        dao.getLevelProgress(gameType) ?: GameLevelProgressEntity(gameType = gameType)

    /** True if [level] is at or below the player's highest unlocked level for this game. */
    suspend fun canPlay(gameType: String, level: Int): Boolean =
        level in 1..getProgress(gameType).highestUnlockedLevel

    /**
     * Called when a level is fully completed. Banks [pointsEarned] into the
     * running total and, if this was the highest unlocked level, unlocks the
     * next one (capped at [GameLevelConfig.LEVEL_COUNT]).
     */
    suspend fun completeLevel(gameType: String, level: Int, pointsEarned: Int) {
        val current = getProgress(gameType)
        val newHighest = if (level >= current.highestUnlockedLevel) {
            (level + 1).coerceAtMost(GameLevelConfig.LEVEL_COUNT)
        } else {
            current.highestUnlockedLevel
        }
        dao.upsertLevelProgress(
            current.copy(
                highestUnlockedLevel = newHighest,
                totalPoints = current.totalPoints + pointsEarned,
                updatedAt = System.currentTimeMillis(),
            )
        )
        dao.clearSessionState(gameType)
    }

    suspend fun saveSession(
        gameType: String,
        level: Int,
        contentSeed: Long,
        stepIndex: Int,
        livePoints: Int,
        snapshotJson: String,
    ) {
        dao.upsertSessionState(
            GameSessionStateEntity(
                gameType = gameType,
                level = level,
                contentSeed = contentSeed,
                stepIndex = stepIndex,
                livePoints = livePoints,
                snapshotJson = snapshotJson,
                updatedAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun loadSession(gameType: String): GameSessionStateEntity? = dao.getSessionState(gameType)

    suspend fun clearSession(gameType: String) = dao.clearSessionState(gameType)
}
