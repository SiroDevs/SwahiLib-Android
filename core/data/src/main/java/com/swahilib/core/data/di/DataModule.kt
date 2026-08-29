package com.swahilib.core.data.di

import android.content.Context
import com.swahilib.core.data.repos.games.EngagementRepo
import com.swahilib.core.data.repos.games.GameProgressRepo
import com.swahilib.core.data.repos.content.HistoryRepo
import com.swahilib.core.data.repos.content.IdiomRepo
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.data.repos.content.ProverbRepo
import com.swahilib.core.data.repos.content.SayingRepo
import com.swahilib.core.data.repos.content.SearchRepo
import com.swahilib.core.data.repos.content.WordRepo
import com.swahilib.core.engagement.engine.AchievementEngine
import com.swahilib.core.engagement.engine.ChallengeEngine
import com.swahilib.core.engagement.engine.ProgressStore
import com.swahilib.core.engagement.engine.RewardsEngine
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.engine.XpEngine
import com.swahilib.core.database.daos.content.HistoryDao
import com.swahilib.core.database.daos.game.GameProgressDao
import com.swahilib.core.database.daos.content.IdiomDao
import com.swahilib.core.database.daos.content.ProverbDao
import com.swahilib.core.database.daos.content.SayingDao
import com.swahilib.core.database.daos.content.SearchDao
import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.engagement.engine.DifficultyEngine
import com.swahilib.core.engagement.engine.RecommendationEngine
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
    fun provideGameProgressRepo(gameProgressDao: GameProgressDao): GameProgressRepo =
        GameProgressRepo(gameProgressDao)

    @Provides @Singleton
    fun provideEngagementRepo(
        prefsRepo: PrefsRepo,
        store: ProgressStore,
        xpEngine: XpEngine,
        rewardsEngine: RewardsEngine,
        challengeEngine: ChallengeEngine,
        achievementEngine: AchievementEngine,
        statisticsEngine: StatisticsEngine,
        difficultyEngine: DifficultyEngine,
        recommendationEngine: RecommendationEngine,
    ): EngagementRepo = EngagementRepo(
        prefsRepo,
        store,
        xpEngine,
        rewardsEngine,
        challengeEngine,
        achievementEngine,
        statisticsEngine,
        difficultyEngine,
        recommendationEngine,
    )
}
