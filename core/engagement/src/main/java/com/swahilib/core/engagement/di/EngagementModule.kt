package com.swahilib.core.engagement.di

import com.swahilib.core.database.daos.AchievementRecordDao
import com.swahilib.core.database.daos.ChallengeDao
import com.swahilib.core.database.daos.DailyActivityDao
import com.swahilib.core.database.daos.LearningHistoryDao
import com.swahilib.core.database.daos.UserProgressDao
import com.swahilib.core.database.daos.XpEventDao
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
