package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.ScrambledWord
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Builds [ScrambledWord] rounds. Progressive difficulty maps to target word
 * length so BEGINNER rounds stay short (<=6 letters) and ADVANCED rounds
 * pull longer words. Endless mode is just repeated calls to [next] with a
 * growing exclusion set so words don't repeat within a session.
 */
@Singleton
class WordBuilderGenerator @Inject constructor(
    private val wordDao: WordDao,
) {

    suspend fun session(
        difficulty: Difficulty = Difficulty.BEGINNER,
        count: Int = 5,
        seed: Long = System.currentTimeMillis(),
    ): List<ScrambledWord> {
        val pool = eligiblePool(difficulty)
        val random = Random(seed)
        return pool.shuffled(random).take(count.coerceAtMost(pool.size))
            .mapIndexed { index, word -> scramble(word, index, random) }
    }

    /** For endless mode: one more round, excluding words already used this session. */
    suspend fun next(
        difficulty: Difficulty,
        usedRids: Set<Int>,
        seed: Long = System.currentTimeMillis(),
    ): ScrambledWord? {
        val pool = eligiblePool(difficulty).filter { it.rid !in usedRids }
        val word = pool.randomOrNull(Random(seed)) ?: return null
        return scramble(word, index = usedRids.size, Random(seed))
    }

    private suspend fun eligiblePool(difficulty: Difficulty): List<WordEntity> {
        val all = wordDao.getAll().first().filter { !it.title.isNullOrBlank() && it.title!!.trim().length >= 3 }
        val lengthRange = when (difficulty) {
            Difficulty.BEGINNER -> 3..6
            Difficulty.INTERMEDIATE -> 5..9
            Difficulty.ADVANCED -> 7..99
        }
        val filtered = all.filter { it.title!!.trim().length in lengthRange }
        // Fall back to the full pool if the difficulty band is too sparse for this dictionary.
        return filtered.ifEmpty { all }
    }

    private fun scramble(word: WordEntity, index: Int, random: Random): ScrambledWord {
        val answer = word.title!!.trim()
        var letters: List<Char>
        do {
            letters = answer.toList().shuffled(random)
        } while (letters.joinToString("") == answer && answer.length > 1)

        return ScrambledWord(
            id = "wb_${word.rid}_$index",
            answer = answer,
            scrambledLetters = letters,
            hint = word.meaning?.takeIf { it.isNotBlank() } ?: word.english?.takeIf { it.isNotBlank() } ?: "",
            sourceWordRid = word.rid,
        )
    }
}
