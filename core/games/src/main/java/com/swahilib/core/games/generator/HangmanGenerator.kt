package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.HangmanRound
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class HangmanGenerator @Inject constructor(
    private val wordDao: WordDao,
) {
    suspend fun session(
        difficulty: Difficulty = Difficulty.BEGINNER,
        count: Int = 5,
        seed: Long = System.currentTimeMillis(),
    ): List<HangmanRound> {
        val random = Random(seed)
        val lengthRange = when (difficulty) {
            Difficulty.BEGINNER -> 3..6
            Difficulty.INTERMEDIATE -> 5..9
            Difficulty.ADVANCED -> 7..99
        }
        val all = wordDao.getAll().first().filter { word ->
            val title = word.title?.trim().orEmpty()
            title.isNotBlank() && !word.definitionText().isNullOrBlank()
        }
        val pool = all.filter { it.title!!.trim().length in lengthRange }.ifEmpty { all }

        return pool.shuffled(random).take(count.coerceAtMost(pool.size)).map { word ->
            HangmanRound(
                answer = word.title!!.trim().uppercase(),
                hint = word.definitionText().orEmpty(),
                sourceWordRid = word.rid,
            )
        }
    }
}
