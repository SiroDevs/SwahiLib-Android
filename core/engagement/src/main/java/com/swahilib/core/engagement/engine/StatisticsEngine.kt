package com.swahilib.core.engagement.engine

import com.swahilib.core.database.model.DailyActivityEntity
import com.swahilib.core.database.model.LearningHistoryEntity
import com.swahilib.core.engagement.model.DailyActivitySnapshot
import com.swahilib.core.engagement.model.StatisticsSummary
import com.swahilib.core.engagement.time.TimeKeys
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Aggregates persisted engagement data into a shape the dashboard renders
 * directly. Everything here is a read; mutations belong on the individual
 * engines (XP / Rewards / Challenge) so the counters stay authoritative.
 */
@Singleton
class StatisticsEngine @Inject constructor(
    private val store: ProgressStore,
) {

    /** Aggregate summary + last-7-day activity chart. */
    suspend fun summary(): StatisticsSummary {
        val progress = store.loadOrInitProgress()
        val from = TimeKeys.daysAgoKey(6, store.clock)
        val to = TimeKeys.today(store.clock)
        val daysRange = store.dailyActivityDao.getRange(from, to)
        val activeDays = store.dailyActivityDao.activeDaysBetween(from, to)

        val byDate = daysRange.associateBy { it.date }
        val weeklyActivity = (6 downTo 0).map { daysAgo ->
            val key = TimeKeys.daysAgoKey(daysAgo, store.clock)
            byDate[key]?.toSnapshot() ?: DailyActivitySnapshot(
                date = key,
                xpEarned = 0,
                activitiesCompleted = 0,
                secondsSpent = 0,
                quizzesCorrect = 0,
                quizzesTotal = 0,
                wordsLearned = 0,
                gamesPlayed = 0,
            )
        }

        val totalQuizzes = daysRange.sumOf { it.quizzesTotal.toLong() }
        val totalCorrect = daysRange.sumOf { it.quizzesCorrect.toLong() }
        val accuracy = if (totalQuizzes == 0L) 0f
        else (totalCorrect.toFloat() / totalQuizzes.toFloat()).coerceIn(0f, 1f)

        return StatisticsSummary(
            totalLearningSeconds = progress.totalLearningSeconds,
            quizAccuracy = accuracy,
            gamesPlayed = progress.gamesPlayed,
            wordsLearned = progress.wordsLearned,
            weeklyActivity = weeklyActivity,
            activeDaysThisWeek = activeDays,
        )
    }

    /**
     * Records a completed learning event and rolls its counters into the
     * daily-activity row + user progress totals. Call from every game/quiz
     * on completion so the dashboard reflects reality.
     */
    suspend fun recordEvent(
        type: EventType,
        title: String,
        referenceId: String? = null,
        score: Int? = null,
        maxScore: Int? = null,
        xpEarned: Int = 0,
        secondsSpent: Int = 0,
    ) {
        val today = TimeKeys.today(store.clock)
        store.learningHistoryDao.insert(
            LearningHistoryEntity(
                type = type.name,
                title = title,
                referenceId = referenceId,
                score = score,
                maxScore = maxScore,
                xpEarned = xpEarned,
                secondsSpent = secondsSpent,
                date = today,
                createdAt = store.clock.now(),
            )
        )
        store.mutateDay(today) { day ->
            day.copy(
                quizzesCorrect = day.quizzesCorrect + (score ?: 0),
                quizzesTotal = day.quizzesTotal + (maxScore ?: 0),
                wordsLearned = day.wordsLearned + type.wordsLearnedIncrement(),
                gamesPlayed = day.gamesPlayed + type.gamePlayedIncrement(),
                visited = true,
            )
        }
        val progress = store.loadOrInitProgress()
        store.writeProgress(
            progress.copy(
                wordsLearned = progress.wordsLearned + type.wordsLearnedIncrement(),
                gamesPlayed = progress.gamesPlayed + type.gamePlayedIncrement(),
            )
        )
    }

    enum class EventType(
        private val gamePlayed: Boolean,
        private val wordsLearned: Int,
    ) {
        QUIZ(gamePlayed = true, wordsLearned = 0),
        WORD_BUILDER(gamePlayed = true, wordsLearned = 1),
        CROSSWORD(gamePlayed = true, wordsLearned = 2),
        WORD_SEARCH(gamePlayed = true, wordsLearned = 1),
        SENTENCE_BUILDER(gamePlayed = true, wordsLearned = 0),
        SPELLING(gamePlayed = true, wordsLearned = 1),
        PROVERB(gamePlayed = false, wordsLearned = 0),
        WORD_LOOKUP(gamePlayed = false, wordsLearned = 1),
        DAILY_READ(gamePlayed = false, wordsLearned = 1),
        ;

        fun gamePlayedIncrement(): Int = if (gamePlayed) 1 else 0
        fun wordsLearnedIncrement(): Int = wordsLearned
    }
}

private fun DailyActivityEntity.toSnapshot(): DailyActivitySnapshot = DailyActivitySnapshot(
    date = date,
    xpEarned = xpEarned,
    activitiesCompleted = activitiesCompleted,
    secondsSpent = secondsSpent,
    quizzesCorrect = quizzesCorrect,
    quizzesTotal = quizzesTotal,
    wordsLearned = wordsLearned,
    gamesPlayed = gamesPlayed,
)
