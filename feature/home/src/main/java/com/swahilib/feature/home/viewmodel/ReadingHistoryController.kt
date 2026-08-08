package com.swahilib.feature.home.viewmodel

import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.database.model.HistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Records "items you opened" reading history. Display, grouping, and clearing of this history
 * now lives in the standalone feature:history module (its own [HistoryRepo] instance) - this
 * controller only keeps the write-path Search still needs when a result is tapped.
 */
class ReadingHistoryController(
    private val historyRepo: HistoryRepo,
    private val scope: CoroutineScope,
) {
    fun addToHistory(itemId: Int, type: String) {
        scope.launch {
            val entry = HistoryEntity(
                item = itemId,
                type = type,
                createdAt = System.currentTimeMillis().toString()
            )
            historyRepo.saveHistory(entry)
        }
    }
}
