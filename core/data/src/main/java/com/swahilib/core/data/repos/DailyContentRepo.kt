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

    /**
     * Every past daily selection, most recent first, with the word/proverb
     * resolved from their rids for display. Used by DailyContentHistoryScreen.
     */
    suspend fun getHistory(): List<DailyContentHistoryEntry> = withContext(Dispatchers.IO) {
        dailyContentDao.getAll().map { entity ->
            DailyContentHistoryEntry(
                date = entity.date,
                word = wordDao.getByRid(entity.wordRid),
                wordMeaning = entity.wordMeaning,
                proverb = proverbDao.getByRid(entity.proverbRid),
                proverbMeaning = entity.proverbMeaning,
            )
        }
    }

    /** Wipes the daily word/proverb selection history (does not touch the word/proverb dictionary itself). */
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        dailyContentDao.deleteAll()
    }
}

/** One resolved day of daily-content history, ready for display. */
data class DailyContentHistoryEntry(
    val date: String,
    val word: WordEntity?,
    val wordMeaning: String,
    val proverb: ProverbEntity?,
    val proverbMeaning: String,
)
