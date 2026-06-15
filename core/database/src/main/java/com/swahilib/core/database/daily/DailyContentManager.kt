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

    /** Result bundle with the resolved entities for convenience. */
    data class DailyContent(
        val entity: DailyContentEntity,
        val word: WordEntity?,
        val proverb: ProverbEntity?,
    )

    /**
     * Returns today's daily content, generating + persisting a fresh
     * selection if none exists yet for [today].
     */
    suspend fun getOrCreateToday(
        dailyContentDao: DailyContentDao,
        wordDao: WordDao,
        proverbDao: ProverbDao,
    ): DailyContent {
        val today = todayKey()
        val existing = dailyContentDao.get()

        val entity = if (existing != null && existing.date == today) {
            existing
        } else {
            val word = wordDao.getRandomWord()
            val proverb = proverbDao.getRandomProverb()

            val fresh = DailyContentEntity(
                date = today,
                wordRid = word?.rid ?: 0,
                wordMeaning = pickRandomMeaning(word?.meaning, "|"),
                proverbRid = proverb?.rid ?: 0,
                proverbMeaning = pickRandomMeaning(proverb?.meaning, "|", "#"),
            )
            dailyContentDao.upsert(fresh)
            fresh
        }

        val word = wordDao.getByRid(entity.wordRid)
        val proverb = proverbDao.getByRid(entity.proverbRid)
        return DailyContent(entity = entity, word = word, proverb = proverb)
    }

    /** Splits [meaning] on any of [delimiters] and returns one entry at random. */
    private fun pickRandomMeaning(meaning: String?, vararg delimiters: String): String =
        meaning
            ?.split(*delimiters)
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.randomOrNull()
            ?: ""

    /** UTC-based yyyy-MM-dd key so the "day" rolls over consistently. */
    private fun todayKey(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        return formatter.format(Date())
    }
}
