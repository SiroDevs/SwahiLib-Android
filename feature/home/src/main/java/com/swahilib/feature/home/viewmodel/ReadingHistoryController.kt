package com.swahilib.feature.home.viewmodel

import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.database.model.HistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Owns the "items you opened" reading history (distinct from search-text history). */
class ReadingHistoryController(
    private val historyRepo: HistoryRepo,
    private val scope: CoroutineScope,
) {
    private val _history = MutableStateFlow<List<HistoryEntity>>(emptyList())
    val history: StateFlow<List<HistoryEntity>> get() = _history

    fun refreshHistory() {
        scope.launch {
            _history.value = historyRepo.fetchLocalData().sortedByDescending { it.createdAt }
        }
    }

    fun clearReadingHistory() {
        scope.launch {
            historyRepo.clearAll()
            _history.value = emptyList()
        }
    }

    fun addToHistory(itemId: Int, type: String) {
        scope.launch {
            val entry = HistoryEntity(
                item = itemId,
                type = type,
                createdAt = System.currentTimeMillis().toString()
            )
            historyRepo.saveHistory(entry)
            _history.value = listOf(entry) + _history.value
        }
    }
}
