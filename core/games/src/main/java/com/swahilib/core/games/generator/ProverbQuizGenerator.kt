package com.swahilib.core.games.generator

import com.swahilib.core.database.daos.content.ProverbDao
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.QuizFormat
import com.swahilib.core.games.model.QuizOption
import com.swahilib.core.games.model.QuizQuestion
import com.swahilib.core.games.model.QuizSet
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ProverbQuizGenerator @Inject constructor(
    private val proverbDao: ProverbDao,
) {

    suspend fun generate(
        difficulty: Difficulty = Difficulty.BEGINNER,
        questionCount: Int = 5,
        seed: Long = System.currentTimeMillis(),
    ): QuizSet {
        val random = Random(seed)
        val pool = proverbDao.getAll().first().filter { !it.title.isNullOrBlank() && !it.meaning.isNullOrBlank() }

        if (pool.isEmpty()) {
            return QuizSet(id = "proverb_$seed", difficulty = difficulty, questions = emptyList())
        }

        val targets = pool.shuffled(random).take(questionCount.coerceAtMost(pool.size))
        val questions = targets.mapIndexed { index, proverb ->
            val distractorPool = pool.filter { it.rid != proverb.rid }
            if (index % 2 == 0 || distractorPool.size < 3) {
                buildTrueFalse(proverb, distractorPool, index, random)
            } else {
                buildMultipleChoice(proverb, distractorPool, index, random)
            }
        }
        return QuizSet(id = "proverb_$seed", difficulty = difficulty, questions = questions)
    }

    private fun buildMultipleChoice(
        proverb: ProverbEntity,
        distractorPool: List<ProverbEntity>,
        index: Int,
        random: Random,
    ): QuizQuestion {
        val correctText = proverb.meaning.orEmpty()
        val distractors = distractorPool.shuffled(random).mapNotNull { it.meaning }
            .filter { it != correctText }.distinct().take(3)
        val options = (distractors + correctText).shuffled(random)
            .mapIndexed { i, text -> QuizOption(id = "opt_$i", text = text) }
        val correctId = options.first { it.text == correctText }.id

        return QuizQuestion(
            id = "pv_mc_${proverb.rid}_$index",
            format = QuizFormat.MULTIPLE_CHOICE,
            prompt = "Methali \"${proverb.title}\" ina maana gani?",
            options = options,
            correctOptionIds = setOf(correctId),
            explanation = "\"${proverb.title}\" maana yake ni \"$correctText\".",
            sourceWordRid = proverb.rid,
        )
    }

    private fun buildTrueFalse(
        proverb: ProverbEntity,
        distractorPool: List<ProverbEntity>,
        index: Int,
        random: Random,
    ): QuizQuestion {
        val showTrue = random.nextBoolean()
        val correctText = proverb.meaning.orEmpty()
        val shownText = if (showTrue) {
            correctText
        } else {
            distractorPool.shuffled(random).firstNotNullOfOrNull { it.meaning } ?: correctText
        }

        return QuizQuestion(
            id = "pv_tf_${proverb.rid}_$index",
            format = QuizFormat.TRUE_FALSE,
            prompt = "Kweli au Uongo: Methali \"${proverb.title}\" inamaanisha \"$shownText\".",
            options = listOf(QuizOption("true", "Kweli"), QuizOption("false", "Uongo")),
            correctOptionIds = setOf(if (showTrue) "true" else "false"),
            explanation = "\"${proverb.title}\" maana yake halisi ni \"$correctText\".",
            sourceWordRid = proverb.rid,
        )
    }
}
