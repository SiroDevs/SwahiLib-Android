package com.swahilib.core.games.engine

import com.swahilib.core.engagement.model.Difficulty

object GameLevelConfig {
    const val LEVEL_COUNT = 5
    const val DEFAULT_STEP_COUNT = 6

    fun timerSecondsForLevel(level: Int): Int {
        val clamped = level.coerceIn(1, LEVEL_COUNT)
        val start = 120
        val end = 30
        val step = (start - end) / (LEVEL_COUNT - 1)
        val raw = start - (step * (clamped - 1))
        return (raw / 5) * 5
    }

    fun pointsPerCorrect(level: Int): Int = 10 * level.coerceIn(1, LEVEL_COUNT)

    fun stepCountForLevel(level: Int): Int = DEFAULT_STEP_COUNT

    fun bannerPointsForLevel(level: Int): Int = pointsPerCorrect(level) * stepCountForLevel(level)

    fun difficultyForLevel(level: Int): Difficulty = when (level.coerceIn(1, LEVEL_COUNT)) {
        1, 2 -> Difficulty.BEGINNER
        3 -> Difficulty.INTERMEDIATE
        else -> Difficulty.ADVANCED
    }

    fun isEasyLevel(level: Int): Boolean = level <= 2

    fun levels(): IntRange = 1..LEVEL_COUNT
}
