package com.swahilib.core.database.daily

import com.swahilib.core.database.daos.DailyContentDao
import com.swahilib.core.database.daos.ProverbDao
import com.swahilib.core.database.daos.WordDao
import com.swahilib.core.database.model.DailyContentEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.WordEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DailyContentManager {
    data class DailyContent(
        val entity: DailyContentEntity,
        val word: WordEntity?,
        val proverb: ProverbEntity?,
    )

    suspend fun getOrCreateToday(
        dailyContentDao: DailyContentDao,
        wordDao: WordDao,
        proverbDao: ProverbDao,
    ): DailyContent {
        val today = todayKey()
        val existing = dailyContentDao.getByDate(today)

        val entity = existing ?: run {
            val word = wordDao.getRandomWord()
            val proverb = proverbDao.getRandomProverb()

            val fresh = DailyContentEntity(
                date = today,
                wordRid = word?.rid ?: 0,
                wordMeaning = pickRandomMeaning(word?.meaning, "|"),
                proverbRid = proverb?.rid ?: 0,
                proverbMeaning = pickRandomMeaning(proverb?.meaning, "|", "#"),
            )
            dailyContentDao.insert(fresh)
            dailyContentDao.getByDate(today) ?: fresh
        }

        val word = wordDao.getByRid(entity.wordRid)
        val proverb = proverbDao.getByRid(entity.proverbRid)
        return DailyContent(entity = entity, word = word, proverb = proverb)
    }

    private fun pickRandomMeaning(meaning: String?, vararg delimiters: String): String =
        meaning
            ?.split(*delimiters)
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.randomOrNull()
            ?: ""

    private fun todayKey(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        return formatter.format(Date())
    }
}
