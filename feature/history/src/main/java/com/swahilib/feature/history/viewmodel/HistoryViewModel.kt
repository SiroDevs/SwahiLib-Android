package com.swahilib.feature.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.entities.content.HistoryEntity
import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.database.entities.content.SearchEntity
import com.swahilib.core.database.entities.content.WordEntity
import com.swahilib.feature.history.model.ContentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the "Historia" screen: reading history (resolved to actual word/idiom/proverb/saying
 * content), search-text history, and the derived spaced-repetition review nudge. Independent from
 * [com.swahilib.feature.home.viewmodel.HomeViewModel] - each repo is injected directly here.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepo: HistoryRepo,
    private val searchRepo: SearchRepo,
    private val wordRepo: WordRepo,
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
) : ViewModel() {

    /** A reading-history row already resolved to its content, ready for display. */
    data class ResolvedHistoryItem(val history: HistoryEntity, val content: ContentItem?)

    private val _resolvedHistory = MutableStateFlow<List<ResolvedHistoryItem>>(emptyList())
    val resolvedHistory: StateFlow<List<ResolvedHistoryItem>> = _resolvedHistory.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchEntity>>(emptyList())
    val searchHistory: StateFlow<List<SearchEntity>> = _searchHistory.asStateFlow()

    /**
     * Lightweight spaced-repetition nudge: words the user searched for at least
     * [REVIEW_MIN_AGE_MS] ago (so it's not just re-showing what they searched a minute ago).
     * Surfacing real past lookups ties the nudge to something they actually needed.
     */
    val reviewSuggestions: StateFlow<List<SearchEntity>>
        get() = _searchHistory.map { searches ->
            val cutoff = System.currentTimeMillis() - REVIEW_MIN_AGE_MS
            searches
                .filter { (it.createdAt.toLongOrNull() ?: Long.MAX_VALUE) < cutoff }
                .distinctBy { it.title.lowercase() }
                .sortedByDescending { it.createdAt.toLongOrNull() ?: 0L }
                .take(REVIEW_MAX_ITEMS)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        refreshHistory()
        refreshSearchHistory()
    }

    fun refreshHistory() {
        viewModelScope.launch {
            val rows = historyRepo.fetchLocalData().sortedByDescending { it.createdAt.toLongOrNull() ?: 0L }
            _resolvedHistory.value = rows.map { h -> ResolvedHistoryItem(h, resolve(h.type, h.item)) }
        }
    }

    fun refreshSearchHistory() {
        viewModelScope.launch {
            _searchHistory.value = searchRepo.fetchLocalData()
                .sortedByDescending { it.createdAt.toLongOrNull() ?: 0L }
        }
    }

    fun clearReadingHistory() {
        viewModelScope.launch {
            historyRepo.clearAll()
            _resolvedHistory.value = emptyList()
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchRepo.clearAll()
            _searchHistory.value = emptyList()
        }
    }

    fun likeWord(word: WordEntity) {
        viewModelScope.launch {
            val updated = word.copy(liked = !word.liked)
            wordRepo.updateWord(updated)
            replaceResolved<ContentItem.Word>(word.rid) { ContentItem.Word(updated) }
        }
    }

    fun likeIdiom(idiom: IdiomEntity) {
        viewModelScope.launch {
            val updated = idiom.copy(liked = !idiom.liked)
            idiomRepo.updateIdiom(updated)
            replaceResolved<ContentItem.Idiom>(idiom.rid) { ContentItem.Idiom(updated) }
        }
    }

    fun likeProverb(proverb: ProverbEntity) {
        viewModelScope.launch {
            val updated = proverb.copy(liked = !proverb.liked)
            proverbRepo.updateProverb(updated)
            replaceResolved<ContentItem.Proverb>(proverb.rid) { ContentItem.Proverb(updated) }
        }
    }

    fun likeSaying(saying: SayingEntity) {
        viewModelScope.launch {
            val updated = saying.copy(liked = !saying.liked)
            sayingRepo.updateSaying(updated)
            replaceResolved<ContentItem.Saying>(saying.rid) { ContentItem.Saying(updated) }
        }
    }

    /** Replaces a resolved row's content, matching on both content type [T] and rid - two
     * different tables (e.g. a word and an idiom) can share the same numeric rid, so type must
     * be part of the match. */
    private inline fun <reified T : ContentItem> replaceResolved(rid: Int, replacement: () -> T) {
        _resolvedHistory.value = _resolvedHistory.value.map { row ->
            val content = row.content
            if (content !is T) return@map row
            val currentRid = when (content) {
                is ContentItem.Word -> content.entity.rid
                is ContentItem.Idiom -> content.entity.rid
                is ContentItem.Proverb -> content.entity.rid
                is ContentItem.Saying -> content.entity.rid
            }
            if (currentRid == rid) row.copy(content = replacement()) else row
        }
    }

    private suspend fun resolve(type: String, id: Int): ContentItem? = when (type) {
        "word" -> wordRepo.getWordByRid(id)?.let { ContentItem.Word(it) }
        "idiom" -> idiomRepo.getIdiomByRid(id)?.let { ContentItem.Idiom(it) }
        "proverb" -> proverbRepo.getProverbByRid(id)?.let { ContentItem.Proverb(it) }
        "saying" -> sayingRepo.getSayingByRid(id)?.let { ContentItem.Saying(it) }
        else -> null
    }

    companion object {
        private const val REVIEW_MIN_AGE_MS = 3 * 24 * 60 * 60 * 1000L // 3 days
        private const val REVIEW_MAX_ITEMS = 8
    }
}
