package com.swahilib.core.games.engine

import com.swahilib.core.engagement.model.Difficulty

/**
 * Shared "kiwango" (level) curve used by every non-Quiz engagement game
 * (Hangman, Word Builder, Sentence Builder, Spelling, Crossword, Word
 * Search). Quiz intentionally has no levels - see QuizViewModel.
 *
 * Levels are strictly sequential: finishing level N unlocks level N+1, and a
 * player can always replay any level at or below their highest unlocked one,
 * but never skip ahead (enforced by [GameLevelUiModel.unlocked] /
 * `GameProgressRepo.canPlay`).
 */
object GameLevelConfig {
    const val LEVEL_COUNT = 5
    const val DEFAULT_STEP_COUNT = 6

    /** Seconds allotted per step at this level. Level 1 -> 120s, Level 5 -> 30s, evenly spaced. */
    fun timerSecondsForLevel(level: Int): Int {
        val clamped = level.coerceIn(1, LEVEL_COUNT)
        val start = 120
        val end = 30
        val step = (start - end) / (LEVEL_COUNT - 1) // 22.5 -> use float then round to nearest 5s
        val raw = start - (step * (clamped - 1))
        return (raw / 5) * 5 // round to a clean multiple of 5 seconds
    }

    /** Points banked per correct step at this level - harder levels are worth more. */
    fun pointsPerCorrect(level: Int): Int = 10 * level.coerceIn(1, LEVEL_COUNT)

    /** How many steps (questions/words/rounds) make up a session at this level. */
    fun stepCountForLevel(level: Int): Int = DEFAULT_STEP_COUNT

    /** The full points banner shown on a level's carousel card - what a flawless run of this level is worth. */
    fun bannerPointsForLevel(level: Int): Int = pointsPerCorrect(level) * stepCountForLevel(level)

    /** Maps a 1..5 kiwango onto the existing BEGINNER/INTERMEDIATE/ADVANCED content curve. */
    fun difficultyForLevel(level: Int): Difficulty = when (level.coerceIn(1, LEVEL_COUNT)) {
        1, 2 -> Difficulty.BEGINNER
        3 -> Difficulty.INTERMEDIATE
        else -> Difficulty.ADVANCED
    }

    /** Whether this level should use the simplified "easy" input aids (letter pool) for Crossword/Word Search. */
    fun isEasyLevel(level: Int): Boolean = level <= 2

    fun levels(): IntRange = 1..LEVEL_COUNT
}
