package com.swahilib.core.engagement.engine

import javax.inject.Inject
import javax.inject.Singleton

data class ActivityRecommendation(
    val type: String, // StatisticsEngine.EventType name
    val reason: String,
    val recentAccuracy: Float?, // null if never attempted
)

/**
 * Sprint 3 - Personalized Learning Paths. Purely data-driven from existing
 * `learning_history` - no separate "interests" tracking exists yet, so
 * "interests" is approximated as "games you haven't tried" (novelty), and
 * "weak vocabulary/grammar" as "lowest recent accuracy among games you
 * have played". Learning pace isn't factored into the picks themselves
 * (there's nothing useful to recommend *harder* off of low pace), but is
 * exposed via [DifficultyEngine] separately for the difficulty-scaling item.
 */
@Singleton
class RecommendationEngine @Inject constructor(
    private val store: ProgressStore,
    private val difficultyEngine: DifficultyEngine,
) {
    companion object {
        private val ALL_TYPES = listOf(
            "QUIZ", "WORD_BUILDER", "SENTENCE_BUILDER", "SPELLING", "CROSSWORD", "WORD_SEARCH", "PROVERB", "HANGMAN",
        )
        private const val REASON_WEAK = "Boresha ustadi wako - hii ndiyo eneo lenye changamoto zaidi kwa sasa"
        private const val REASON_NEW = "Bado hujajaribu mchezo huu - jipe changamoto mpya!"
    }

    suspend fun recommendations(limit: Int = 3): List<ActivityRecommendation> {
        val stats = ALL_TYPES.map { type ->
            val played = store.learningHistoryDao.countByType(type)
            val accuracy = if (played > 0) difficultyEngine.recentAccuracy(type) else null
            Triple(type, played, accuracy)
        }

        val weakestPlayed = stats.filter { it.second > 0 && it.third != null }
            .sortedBy { it.third }
            .map { (type, _, accuracy) -> ActivityRecommendation(type, REASON_WEAK, accuracy) }

        val neverTried = stats.filter { it.second == 0 }
            .map { (type, _, _) -> ActivityRecommendation(type, REASON_NEW, null) }

        // Interleave: lead with the weakest area (most actionable), then one novel suggestion, then more weak areas.
        val combined = buildList {
            weakestPlayed.firstOrNull()?.let { add(it) }
            neverTried.firstOrNull()?.let { add(it) }
            addAll(weakestPlayed.drop(1))
            addAll(neverTried.drop(1))
        }.distinctBy { it.type }.take(limit)

        return combined.ifEmpty { listOf(ActivityRecommendation("QUIZ", "Anza safari yako ya kujifunza!", null)) }
    }
}
