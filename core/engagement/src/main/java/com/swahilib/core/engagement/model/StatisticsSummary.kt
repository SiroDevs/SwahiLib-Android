package com.swahilib.core.engagement.model

data class StatisticsSummary(
    val totalLearningSeconds: Long,
    val quizAccuracy: Float,
    val gamesPlayed: Int,
    val wordsLearned: Int,
    val weeklyActivity: List<DailyActivitySnapshot>,
    val activeDaysThisWeek: Int,
)
