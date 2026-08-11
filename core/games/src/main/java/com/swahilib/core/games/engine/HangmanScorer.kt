package com.swahilib.core.games.engine

import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.games.model.HangmanRound
import com.swahilib.core.games.model.HangmanSessionResult

object HangmanScorer {
    fun guess(round: HangmanRound, letter: Char): HangmanRound {
        if (round.isOver || letter in round.guessedLetters) return round
        val correct = letter in round.answer
        return round.copy(
            guessedLetters = round.guessedLetters + letter,
            wrongGuesses = if (correct) round.wrongGuesses else round.wrongGuesses + 1,
        )
    }

    fun tally(rounds: List<HangmanRound>, difficulty: Difficulty, secondsSpent: Int): HangmanSessionResult {
        val total = rounds.size
        val won = rounds.count { it.isWon }
        val xpPerWord = RewardRules.activityXp(ActivityType.HANGMAN, difficulty) / total.coerceAtLeast(1)

        return HangmanSessionResult(
            totalWords = total,
            wonWords = won,
            xpEarned = (xpPerWord * won).coerceAtLeast(if (won > 0) 1 else 0),
            secondsSpent = secondsSpent,
            isPerfect = total > 0 && won == total,
        )
    }
}
