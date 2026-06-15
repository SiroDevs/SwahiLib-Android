package com.swahilib.core.data.repos

import com.swahilib.core.database.daily.DailyContentManager
import com.swahilib.core.database.daos.DailyContentDao
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for "Neno la Siku" (Word of the Day) and
 * "Methali ya Siku" (Proverb of the Day).
 *
 * The first caller of the day (a [com.swahilib.core.data.notifications.DailyWordWorker]
 * / [com.swahilib.core.data.notifications.DailyProverbWorker], the
 * "Neno la Siku" / "Methali ya Siku" screens, or the home-screen widget)
 * generates and persists today's pick; everyone else for the rest of the
 * day reads back that exact same word, proverb, and chosen meaning.
 *
 * The widget can't take part in Hilt's dependency graph, so it calls
 * [DailyContentManager] directly with DAOs obtained from
 * `AppDatabase.getInstanceForWidget(context)`. This repo simply delegates
 * to the same shared logic for everything else.
 */
@Singleton
class DailyContentRepo @Inject constructor(
    private val dailyContentDao: DailyContentDao,
    private val wordDao: WordDao,
    private val proverbDao: ProverbDao,
) {

    /** Today's Word of the Day plus the single meaning chosen for it. */
    suspend fun getDailyWord(): Pair<WordEntity?, String> = withContext(Dispatchers.IO) {
        val daily = DailyContentManager.getOrCreateToday(dailyContentDao, wordDao, proverbDao)
        daily.word to daily.entity.wordMeaning
    }

    /** Today's Proverb of the Day plus the single meaning chosen for it. */
    suspend fun getDailyProverb(): Pair<ProverbEntity?, String> = withContext(Dispatchers.IO) {
        val daily = DailyContentManager.getOrCreateToday(dailyContentDao, wordDao, proverbDao)
        daily.proverb to daily.entity.proverbMeaning
    }
}
