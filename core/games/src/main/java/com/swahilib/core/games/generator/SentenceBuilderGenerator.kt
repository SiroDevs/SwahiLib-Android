package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.SentenceQuestion
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SentenceBuilderGenerator @Inject constructor(
    private val proverbDao: ProverbDao,
) {

    suspend fun generate(
        difficulty: Difficulty = Difficulty.BEGINNER,
        count: Int = 5,
        seed: Long = System.currentTimeMillis(),
    ): List<SentenceQuestion> {
        val random = Random(seed)
        val wordRange = when (difficulty) {
            Difficulty.BEGINNER -> 2..5
            Difficulty.INTERMEDIATE -> 4..7
            Difficulty.ADVANCED -> 6..99
        }

        val pool = proverbDao.getAll().first().filter { proverb ->
            val words = proverb.title?.trim()?.split(Regex("\\s+")).orEmpty()
            words.size in wordRange && !proverb.meaning.isNullOrBlank()
        }
        // Widen to any multi-word proverb if the difficulty band is too sparse.
        val effectivePool = pool.ifEmpty {
            proverbDao.getAll().first().filter {
                (it.title?.trim()?.split(Regex("\\s+"))?.size ?: 0) >= 2 && !it.meaning.isNullOrBlank()
            }
        }

        return effectivePool.shuffled(random).take(count.coerceAtMost(effectivePool.size))
            .mapIndexedNotNull { index, proverb ->
                val words = proverb.title?.trim()?.split(Regex("\\s+")) ?: return@mapIndexedNotNull null
                var shuffled: List<String>
                do {
                    shuffled = words.shuffled(random)
                } while (shuffled == words && words.size > 1)

                SentenceQuestion(
                    id = "sb_${proverb.rid}_$index",
                    shuffledWords = shuffled,
                    correctOrder = words,
                    explanation = proverb.meaning.orEmpty(),
                    sourceRid = proverb.rid,
                )
            }
    }
}
