package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.SpellingQuestion
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SpellingGenerator @Inject constructor(
    private val wordDao: WordDao,
) {
    suspend fun generate(
        difficulty: Difficulty = Difficulty.BEGINNER,
        count: Int = 5,
        seed: Long = System.currentTimeMillis(),
    ): List<SpellingQuestion> {
        val random = Random(seed)
        val lengthRange = when (difficulty) {
            Difficulty.BEGINNER -> 3..6
            Difficulty.INTERMEDIATE -> 5..9
            Difficulty.ADVANCED -> 7..99
        }

        val all = wordDao.getAll().first().filter { word ->
            val title = word.title?.trim().orEmpty()
            title.isNotBlank() && (!word.meaning.isNullOrBlank() || !word.english.isNullOrBlank())
        }
        val pool = all.filter { it.title!!.trim().length in lengthRange }.ifEmpty { all }

        return pool.shuffled(random).take(count.coerceAtMost(pool.size)).mapIndexed { index, word ->
            SpellingQuestion(
                id = "sp_${word.rid}_$index",
                clue = word.meaning?.takeIf { it.isNotBlank() } ?: word.english.orEmpty(),
                answer = word.title!!.trim(),
                sourceRid = word.rid,
            )
        }
    }
}
