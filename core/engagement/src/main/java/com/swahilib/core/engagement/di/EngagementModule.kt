package com.swahilib.core.engagement.di

import com.swahilib.core.database.daos.game.AchievementRecordDao
import com.swahilib.core.database.daos.game.ChallengeDao
import com.swahilib.core.database.daos.daily.DailyActivityDao
import com.swahilib.core.database.daos.game.LearningHistoryDao
import com.swahilib.core.database.daos.game.UserProgressDao
import com.swahilib.core.database.daos.game.XpEventDao
import com.swahilib.core.engagement.engine.ProgressStore
import com.swahilib.core.engagement.time.Clock
import com.swahilib.core.engagement.time.SystemClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object EngagementModule {

    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock

    @Provides
    @Singleton
    fun provideProgressStore(
        userProgressDao: UserProgressDao,
        xpEventDao: XpEventDao,
        achievementRecordDao: AchievementRecordDao,
        challengeDao: ChallengeDao,
        dailyActivityDao: DailyActivityDao,
        learningHistoryDao: LearningHistoryDao,
        clock: Clock,
    ): ProgressStore = ProgressStore(
        userProgressDao = userProgressDao,
        xpEventDao = xpEventDao,
        achievementRecordDao = achievementRecordDao,
        challengeDao = challengeDao,
        dailyActivityDao = dailyActivityDao,
        learningHistoryDao = learningHistoryDao,
        clock = clock,
    )
}
