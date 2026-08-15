/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.core.database

import com.swahilib.core.database.daos.content.ProverbDao
import com.swahilib.core.database.daos.content.WordDao
import com.swahilib.core.database.daos.daily.DailyContentDao
import com.swahilib.core.database.entities.daily.DailyContentEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.WordEntity
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