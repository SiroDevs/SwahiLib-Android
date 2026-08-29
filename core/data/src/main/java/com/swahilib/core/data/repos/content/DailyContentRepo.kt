package com.swahilib.core.data.repos.content

import com.swahilib.core.database.DailyContentManager
import com.swahilib.core.database.daos.daily.DailyContentDao
import com.swahilib.core.database.daos.content.ProverbDao
import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.WordEntity
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

    suspend fun getDailyWord(): Pair<WordEntity?, String> = withContext(Dispatchers.IO) {
        val daily = DailyContentManager.getOrCreateToday(dailyContentDao, wordDao, proverbDao)
        daily.word to daily.entity.wordMeaning
    }

    suspend fun getDailyProverb(): Pair<ProverbEntity?, String> = withContext(Dispatchers.IO) {
        val daily = DailyContentManager.getOrCreateToday(dailyContentDao, wordDao, proverbDao)
        daily.proverb to daily.entity.proverbMeaning
    }

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

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        dailyContentDao.deleteAll()
    }
}

data class DailyContentHistoryEntry(
    val date: String,
    val word: WordEntity?,
    val wordMeaning: String,
    val proverb: ProverbEntity?,
    val proverbMeaning: String,
)
