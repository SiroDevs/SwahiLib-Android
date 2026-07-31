package com.swahilib.core.data.di

import android.content.Context
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.engagement.engine.AchievementEngine
import com.swahilib.core.engagement.engine.ChallengeEngine
import com.swahilib.core.engagement.engine.RewardsEngine
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.engine.XpEngine
import com.swahilib.core.database.daos.HistoryDao
import com.swahilib.core.database.daos.IdiomDao
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.SayingDao
import com.swahilib.core.database.daos.SearchDao
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.network.api.KamusiApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {
    @Provides @Singleton
    fun providePrefsRepo(@ApplicationContext ctx: Context): PrefsRepo = PrefsRepo(ctx)

    @Provides @Singleton
    fun provideWordRepo(wordDao: WordDao, api: KamusiApi): WordRepo =
        WordRepo(wordDao, api)

    @Provides @Singleton
    fun provideIdiomRepo(idiomDao: IdiomDao, api: KamusiApi): IdiomRepo =
        IdiomRepo(idiomDao, api)

    @Provides @Singleton
    fun provideProverbRepo(proverbDao: ProverbDao, api: KamusiApi): ProverbRepo =
        ProverbRepo(proverbDao, api)

    @Provides @Singleton
    fun provideSayingRepo(sayingDao: SayingDao, api: KamusiApi): SayingRepo =
        SayingRepo(sayingDao, api)

    @Provides @Singleton
    fun provideHistoryRepo(historyDao: HistoryDao): HistoryRepo =
        HistoryRepo(historyDao)

    @Provides @Singleton
    fun provideSearchRepo(searchDao: SearchDao): SearchRepo =
        SearchRepo(searchDao)

    @Provides @Singleton
    fun provideEngagementRepo(
        prefsRepo: PrefsRepo,
        xpEngine: XpEngine,
        rewardsEngine: RewardsEngine,
        challengeEngine: ChallengeEngine,
        achievementEngine: AchievementEngine,
        statisticsEngine: StatisticsEngine,
    ): EngagementRepo = EngagementRepo(
        prefsRepo,
        xpEngine,
        rewardsEngine,
        challengeEngine,
        achievementEngine,
        statisticsEngine,
    )
}
