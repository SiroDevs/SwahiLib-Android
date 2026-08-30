package com.swahilib.core.games.generator

import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.database.entities.content.WordEntity
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
class QuizGenerator @Inject constructor(
    private val wordDao: WordDao,
) {

    suspend fun generate(
        difficulty: Difficulty = Difficulty.BEGINNER,
        questionCount: Int = 5,
        seed: Long = System.currentTimeMillis(),
    ): QuizSet {
        val random = Random(seed)
        val pool = wordDao.getAll().first()
            .filter { !it.title.isNullOrBlank() && !it.definitionText().isNullOrBlank() }

        if (pool.isEmpty()) {
            return QuizSet(id = "quiz_$seed", difficulty = difficulty, questions = emptyList())
        }

        val formats = formatsFor(difficulty)
        val includeMatch = QuizFormat.MATCH_WORDS in formats && pool.size >= 4
        val targetCount = (if (includeMatch) questionCount - 1 else questionCount).coerceAtLeast(1)
        val targets = pool.shuffled(random).take(targetCount.coerceAtMost(pool.size))

        val questions = targets.mapIndexedNotNull { index, word ->
            val distractorPool = pool.filter { it.rid != word.rid }
            if (distractorPool.size < 3 && formats.any { it != QuizFormat.FILL_IN_BLANK }) {
                // Not enough distractors for choice-based formats; fall back
                // to fill-in-blank which only needs the target word.
                buildFillInBlank(word, index)
            } else {
                when (formats[index % formats.size]) {
                    QuizFormat.MULTIPLE_CHOICE -> buildMultipleChoice(word, distractorPool, index, random)
                    QuizFormat.TRUE_FALSE -> buildTrueFalse(word, distractorPool, index, random)
                    QuizFormat.FILL_IN_BLANK -> buildFillInBlank(word, index)
                    QuizFormat.MATCH_WORDS -> null // built separately, once per set
                }
            }
        }.toMutableList()

        // MATCH_WORDS is a single multi-pair question rather than one per
        // target, so splice one in for INTERMEDIATE/ADVANCED sets.
        if (includeMatch) {
            val matchWords = pool.shuffled(random).take(4)
            questions.add(
                minOf(2, questions.size),
                buildMatchWords(matchWords, seed),
            )
        }

        return QuizSet(id = "quiz_$seed", difficulty = difficulty, questions = questions)
    }

    private fun formatsFor(difficulty: Difficulty): List<QuizFormat> = when (difficulty) {
        Difficulty.BEGINNER -> listOf(QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE)
        Difficulty.INTERMEDIATE -> listOf(
            QuizFormat.MULTIPLE_CHOICE, QuizFormat.TRUE_FALSE, QuizFormat.FILL_IN_BLANK, QuizFormat.MATCH_WORDS,
        )
        Difficulty.ADVANCED -> listOf(
            QuizFormat.FILL_IN_BLANK, QuizFormat.MULTIPLE_CHOICE, QuizFormat.MATCH_WORDS, QuizFormat.TRUE_FALSE,
        )
    }

    private fun buildMultipleChoice(
        word: WordEntity,
        distractorPool: List<WordEntity>,
        index: Int,
        random: Random,
    ): QuizQuestion {
        val askForMeaning = random.nextBoolean()
        val correctText = word.definitionText().orEmpty()
        val distractors = distractorPool.shuffled(random)
            .mapNotNull { it.definitionText() }
            .filter { it != correctText }
            .distinct()
            .take(3)

        val options = (distractors + correctText).shuffled(random)
            .mapIndexed { i, text -> QuizOption(id = "opt_$i", text = text) }
        val correctId = options.first { it.text == correctText }.id

        val prompt = if (askForMeaning) {
            "Neno \"${word.title}\" lina maana gani?"
        } else {
            "Neno gani lina maana ya: \n \"$correctText\"?"
        }

        return QuizQuestion(
            id = "mc_${word.rid}_$index",
            format = QuizFormat.MULTIPLE_CHOICE,
            prompt = if (askForMeaning) prompt else "Chagua neno sahihi kwa maana hii: \"$correctText\"",
            options = options,
            correctOptionIds = setOf(correctId),
            explanation = "\"${word.title}\" maana yake ni \"$correctText\".",
            sourceWordRid = word.rid,
        )
    }

    private fun buildTrueFalse(
        word: WordEntity,
        distractorPool: List<WordEntity>,
        index: Int,
        random: Random,
    ): QuizQuestion {
        val showTrue = random.nextBoolean()
        val correctText = word.definitionText().orEmpty()
        val shownText = if (showTrue) {
            correctText
        } else {
            distractorPool.shuffled(random).firstNotNullOfOrNull { it.definitionText() } ?: correctText
        }
        val trueOption = QuizOption("true", "Kweli")
        val falseOption = QuizOption("false", "Uongo")

        return QuizQuestion(
            id = "tf_${word.rid}_$index",
            format = QuizFormat.TRUE_FALSE,
            prompt = "Kweli au Uongo: \"${word.title}\" maana yake ni \"$shownText\".",
            options = listOf(trueOption, falseOption),
            correctOptionIds = setOf(if (showTrue) "true" else "false"),
            explanation = "\"${word.title}\" maana yake halisi ni \"$correctText\".",
            sourceWordRid = word.rid,
        )
    }

    private fun buildFillInBlank(word: WordEntity, index: Int): QuizQuestion {
        val meaning = word.definitionText().orEmpty()
        return QuizQuestion(
            id = "fib_${word.rid}_$index",
            format = QuizFormat.FILL_IN_BLANK,
            prompt = "Andika neno la Kiswahili lenye maana ya: \n\"$meaning\"",
            expectedText = word.title.orEmpty(),
            explanation = "Jibu sahihi ni \"${word.title}\".",
            sourceWordRid = word.rid,
        )
    }

    private fun buildMatchWords(words: List<WordEntity>, seed: Long): QuizQuestion {
        val left = words.map { QuizOption(id = it.rid.toString(), text = it.title.orEmpty()) }
        val right = words.shuffled(Random(seed + 1))
            .map { QuizOption(id = it.rid.toString(), text = it.definitionText().orEmpty()) }

        return QuizQuestion(
            id = "match_$seed",
            format = QuizFormat.MATCH_WORDS,
            prompt = "Unganisha kila neno na maana yake sahihi.",
            matchLeft = left,
            matchRight = right,
            explanation = words.joinToString("; ") { "${it.title} = ${it.definitionText()}" },
        )
    }

    private fun WordEntity.definitionText(): String? {
        val meanings = cleanMeaning(meaning).split("|")
        val parts = meanings.firstOrNull()?.split(":")
        val maana = parts?.firstOrNull()?.trim() ?: return english?.takeIf { it.isNotBlank() }
        return maana.takeIf { it.isNotBlank() } ?: english?.takeIf { it.isNotBlank() }
    }
}
