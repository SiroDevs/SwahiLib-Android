package com.swahilib.core.data.repos.games

import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.engagement.engine.AchievementEngine
import com.swahilib.core.engagement.engine.ActivityRecommendation
import com.swahilib.core.engagement.engine.ChallengeEngine
import com.swahilib.core.engagement.engine.DifficultyEngine
import com.swahilib.core.engagement.engine.ProgressStore
import com.swahilib.core.engagement.engine.RecommendationEngine
import com.swahilib.core.engagement.engine.RewardsEngine
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.engine.XpEngine
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.AwardResult
import com.swahilib.core.engagement.model.Challenge
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.StatisticsSummary
import com.swahilib.core.engagement.model.UserProgress
import com.swahilib.core.engagement.model.XpAward
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EngagementRepo @Inject constructor(
    private val prefsRepo: PrefsRepo,
    private val store: ProgressStore,
    private val xpEngine: XpEngine,
    private val rewardsEngine: RewardsEngine,
    private val challengeEngine: ChallengeEngine,
    private val achievementEngine: AchievementEngine,
    private val statisticsEngine: StatisticsEngine,
    private val difficultyEngine: DifficultyEngine,
    private val recommendationEngine: RecommendationEngine,
) {
    suspend fun onAppOpen(): DailyLoginOutcome {
        val streak = prefsRepo.recordDailyVisit()

        challengeEngine.ensureDailyChallenge()
        challengeEngine.ensureWeeklyChallenge()
        challengeEngine.ensureMonthlyChallenge()
        challengeEngine.ensureSeasonalChallenge()

        val streakAchievements = achievementEngine.checkForUnlocksWithStreak(streak)

        val alreadyClaimed = prefsRepo.hasClaimedDailyLoginToday()
        val award = rewardsEngine.grantDailyLogin(streak, alreadyClaimedToday = alreadyClaimed)
        if (award != null) prefsRepo.markDailyLoginClaimed()

        return DailyLoginOutcome(
            streakDay = streak,
            reward = award,
            newAchievements = streakAchievements + (award?.unlockedAchievements ?: emptyList()),
        )
    }

    suspend fun awardXp(award: XpAward): AwardResult = xpEngine.award(award)

    suspend fun grantCoins(amount: Int): Long = rewardsEngine.grantCoins(amount)

    suspend fun spendCoins(amount: Int): Boolean = rewardsEngine.spendCoins(amount)

    suspend fun currentProgress(): UserProgress = xpEngine.currentProgress().copy(
        currentStreak = prefsRepo.currentStreak,
        bestStreak = prefsRepo.bestStreak,
    )

    suspend fun activeChallenges(): List<Challenge> = challengeEngine.activeChallenges()

    suspend fun challenge(id: String): Challenge? = challengeEngine.getById(id)

    suspend fun markActivityComplete(
        challengeId: String,
        activityId: String,
        secondsSpent: Int = 0,
    ): ChallengeEngine.CompletionResult? =
        challengeEngine.markActivityComplete(challengeId, activityId, secondsSpent)

    suspend fun createPracticeSession(): Challenge = challengeEngine.createPracticeSession()

    suspend fun statistics(): StatisticsSummary = statisticsEngine.summary()

    suspend fun achievementsWithStatus(): List<Achievement> = achievementEngine.catalogWithStatus()

    suspend fun recommendedDifficulty(type: StatisticsEngine.EventType): Difficulty =
        difficultyEngine.recommend(type.name)

    suspend fun recommendedActivities(limit: Int = 3): List<ActivityRecommendation> =
        recommendationEngine.recommendations(limit)

    suspend fun recordLearningEvent(
        type: StatisticsEngine.EventType,
        title: String,
        referenceId: String? = null,
        score: Int? = null,
        maxScore: Int? = null,
        xpEarned: Int = 0,
        secondsSpent: Int = 0,
    ): List<Achievement> {
        statisticsEngine.recordEvent(
            type = type,
            title = title,
            referenceId = referenceId,
            score = score,
            maxScore = maxScore,
            xpEarned = xpEarned,
            secondsSpent = secondsSpent,
        )
        return achievementEngine.checkForUnlocksAfterActivity()
    }

    /** Wipes all engagement data (XP, streaks, challenges, achievements, stats) - used by "Futa ChemshaBongo". */
    suspend fun clearAllEngagementData() {
        store.clearAll()
        prefsRepo.resetStreaks()
    }

    data class DailyLoginOutcome(
        val streakDay: Int,
        val reward: AwardResult?,
        val newAchievements: List<Achievement>,
    )
}
