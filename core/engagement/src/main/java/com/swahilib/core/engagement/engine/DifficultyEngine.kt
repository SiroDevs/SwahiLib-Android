package com.swahilib.core.engagement.engine

import com.swahilib.core.engagement.model.Difficulty
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sprint 3 - Difficulty Scaling. Stateless by design: rather than persisting
 * a mutable "current difficulty" that could drift out of sync with reality,
 * every recommendation is recomputed fresh from the last [sampleSize]
 * sessions of a given activity type in `learning_history`. No history yet
 * (or too little to be meaningful) always means BEGINNER - never guess a
 * harder start for a new player.
 */
@Singleton
class DifficultyEngine @Inject constructor(
    private val store: ProgressStore,
) {
    companion object {
        private const val MIN_SAMPLES_TO_ADAPT = 3
        private const val ADVANCED_THRESHOLD = 0.85f
        private const val INTERMEDIATE_THRESHOLD = 0.55f
    }

    /**
     * Recommended starting difficulty for [type] (a `StatisticsEngine.EventType`
     * name, e.g. "QUIZ"), based on average accuracy over the last [sampleSize]
     * completed sessions of that type.
     */
    suspend fun recommend(type: String, sampleSize: Int = 10): Difficulty {
        val recent = store.learningHistoryDao.recentByType(type, sampleSize)
        val accuracies = recent.mapNotNull { entry ->
            val max = entry.maxScore
            val score = entry.score
            if (max != null && max > 0 && score != null) score.toFloat() / max.toFloat() else null
        }
        if (accuracies.size < MIN_SAMPLES_TO_ADAPT) return Difficulty.BEGINNER

        val average = accuracies.average().toFloat()
        return when {
            average >= ADVANCED_THRESHOLD -> Difficulty.ADVANCED
            average >= INTERMEDIATE_THRESHOLD -> Difficulty.INTERMEDIATE
            else -> Difficulty.BEGINNER
        }
    }

    /** Human-readable accuracy summary for [type], for surfacing in UI (e.g. "your last 10: 78%"). */
    suspend fun recentAccuracy(type: String, sampleSize: Int = 10): Float? {
        val recent = store.learningHistoryDao.recentByType(type, sampleSize)
        val accuracies = recent.mapNotNull { entry ->
            val max = entry.maxScore
            val score = entry.score
            if (max != null && max > 0 && score != null) score.toFloat() / max.toFloat() else null
        }
        return if (accuracies.isEmpty()) null else accuracies.average().toFloat()
    }
}
