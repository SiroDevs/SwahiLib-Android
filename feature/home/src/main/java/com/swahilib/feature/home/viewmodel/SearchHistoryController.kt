package com.swahilib.feature.home.viewmodel

import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.database.model.SearchEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Owns what the user has searched for: the debounced history save, and the pending query used to
 * hand a tapped History row back to the Search tab. Display/grouping/clearing of search history,
 * and the "review this again" nudge, now live in the standalone feature:history module.
 */
class SearchHistoryController(
    private val searchRepo: SearchRepo,
    private val scope: CoroutineScope,
) {
    // Set when a search-history entry is tapped (in feature:history) and handed back via
    // Home's savedStateHandle bridge, so HomeSearch can pick it up, pre-fill the field, run
    // the search, then consume it.
    private val _pendingSearchQuery = MutableStateFlow<String?>(null)
    val pendingSearchQuery: StateFlow<String?> = _pendingSearchQuery.asStateFlow()
    fun consumePendingSearchQuery() { _pendingSearchQuery.value = null }
    fun setPendingQuery(query: String) { _pendingSearchQuery.value = query }

    private var searchTrackingJob: Job? = null

    /** Debounced so live-filter-as-you-type keystrokes don't each become a history row. */
    fun trackSearch(rawQuery: String) {
        val trimmed = rawQuery.trim()
        searchTrackingJob?.cancel()
        if (trimmed.length < 2) return
        searchTrackingJob = scope.launch {
            delay(900)
            searchRepo.saveSearch(
                SearchEntity(title = trimmed, createdAt = System.currentTimeMillis().toString())
            )
        }
    }

    /** Cancels any pending debounced save, e.g. when the query is cleared. */
    fun cancelTracking() { searchTrackingJob?.cancel() }
}
