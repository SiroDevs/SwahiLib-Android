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
