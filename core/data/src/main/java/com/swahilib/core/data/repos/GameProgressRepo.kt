package com.swahilib.core.data.repos

import com.swahilib.core.database.daos.GameProgressDao
import com.swahilib.core.database.model.GameLevelProgressEntity
import com.swahilib.core.database.model.GameSessionStateEntity
import com.swahilib.core.games.engine.GameLevelConfig
import javax.inject.Inject

class GameProgressRepo @Inject constructor(
    private val dao: GameProgressDao,
) {
    suspend fun getProgress(gameType: String): GameLevelProgressEntity =
        dao.getLevelProgress(gameType) ?: GameLevelProgressEntity(gameType = gameType)

    suspend fun canPlay(gameType: String, level: Int): Boolean =
        level in 1..getProgress(gameType).highestUnlockedLevel

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
