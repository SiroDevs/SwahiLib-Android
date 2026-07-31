package com.swahilib.feature.home.viewmodel

import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.database.model.SearchEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Owns what the user has searched for: the debounced history save, the
 * derived spaced-repetition "review this again" nudge, and the pending
 * query used to hand a tapped history row back to the Search tab.
 */
class SearchHistoryController(
    private val searchRepo: SearchRepo,
    private val scope: CoroutineScope,
) {
    private val _searchHistory = MutableStateFlow<List<SearchEntity>>(emptyList())
    val searchHistory: StateFlow<List<SearchEntity>> get() = _searchHistory

    /**
     * Lightweight spaced-repetition nudge: words the user searched for at least
     * [REVIEW_MIN_AGE_MS] ago (so it's not just re-showing what they searched a
     * minute ago) and hasn't searched again since. Surfacing real past lookups
     * ties the nudge to something they actually needed, rather than a generic
     * "come back" prompt.
     */
    val reviewSuggestions: StateFlow<List<SearchEntity>>
        get() = _searchHistory.map { searches ->
            val cutoff = System.currentTimeMillis() - REVIEW_MIN_AGE_MS
            searches
                .filter { (it.createdAt.toLongOrNull() ?: Long.MAX_VALUE) < cutoff }
                .distinctBy { it.title.lowercase() }
                .sortedByDescending { it.createdAt.toLongOrNull() ?: 0L }
                .take(REVIEW_MAX_ITEMS)
        }.stateIn(scope, SharingStarted.Lazily, emptyList())

    // Set when a search-history entry is tapped, so HomeSearch can pick it up,
    // pre-fill the field, run the search, then consume it.
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
            refreshSearchHistory()
        }
    }

    /** Cancels any pending debounced save, e.g. when the query is cleared. */
    fun cancelTracking() { searchTrackingJob?.cancel() }

    fun refreshSearchHistory() {
        scope.launch {
            _searchHistory.value = searchRepo.fetchLocalData()
                .sortedByDescending { it.createdAt.toLongOrNull() ?: 0L }
        }
    }

    fun clearSearchHistory() {
        scope.launch {
            searchRepo.clearAll()
            _searchHistory.value = emptyList()
        }
    }

    companion object {
        private const val REVIEW_MIN_AGE_MS = 3 * 24 * 60 * 60 * 1000L // 3 days
        private const val REVIEW_MAX_ITEMS = 8
    }
}
