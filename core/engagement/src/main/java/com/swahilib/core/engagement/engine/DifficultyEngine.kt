package com.swahilib.core.engagement.engine

import com.swahilib.core.engagement.model.Difficulty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DifficultyEngine @Inject constructor(
    private val store: ProgressStore,
) {
    companion object {
        private const val MIN_SAMPLES_TO_ADAPT = 3
        private const val ADVANCED_THRESHOLD = 0.85f
        private const val INTERMEDIATE_THRESHOLD = 0.55f
    }

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
