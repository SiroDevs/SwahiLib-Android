package com.swahilib.core.engagement.model

data class DailyActivitySnapshot(
    val date: String,
    val xpEarned: Long,
    val activitiesCompleted: Int,
    val secondsSpent: Long,
    val quizzesCorrect: Int,
    val quizzesTotal: Int,
    val wordsLearned: Int,
    val gamesPlayed: Int,
)
