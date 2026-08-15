package com.swahilib.feature.advanced_search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.entities.content.HistoryEntity
import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.database.entities.content.WordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { AZ, ZA, LIKED_FIRST }
enum class SearchMode { BEGINNING, MIDDLE, END }

@HiltViewModel
class AdvancedSearchViewModel @Inject constructor(
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
    private val historyRepo: HistoryRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    private val _allIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    private val _allProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    private val _allSayings = MutableStateFlow<List<SayingEntity>>(emptyList())
    private val _allWords = MutableStateFlow<List<WordEntity>>(emptyList())

    private val _filteredIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    private val _filteredProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    private val _filteredSayings = MutableStateFlow<List<SayingEntity>>(emptyList())
    private val _filteredWords = MutableStateFlow<List<WordEntity>>(emptyList())
    val filteredIdioms: StateFlow<List<IdiomEntity>> get() = _filteredIdioms
    val filteredProverbs: StateFlow<List<ProverbEntity>> get() = _filteredProverbs
    val filteredSayings: StateFlow<List<SayingEntity>> get() = _filteredSayings
    val filteredWords: StateFlow<List<WordEntity>> get() = _filteredWords

    fun fetchData(force: Boolean = false) {
        if (!force && _allWords.value.isNotEmpty()) return
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            _allIdioms.value = idiomRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredIdioms.value = _allIdioms.value

            _allProverbs.value = proverbRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredProverbs.value = _allProverbs.value

            _allSayings.value = sayingRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredSayings.value = _allSayings.value

            _allWords.value = wordRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredWords.value = _allWords.value

            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun filterData(query: String, sortOrder: SortOrder, searchMode: SearchMode) {
        when (searchMode) {
            SearchMode.BEGINNING -> searchBeginningOfTerms(query, sortOrder)
            SearchMode.MIDDLE -> searchMiddleOfTerms(query, sortOrder)
            SearchMode.END -> searchEndOfTerms(query, sortOrder)
        }
    }

    fun searchBeginningOfTerms(query: String, sortOrder: SortOrder = SortOrder.AZ) {
        val q = query.lowercase().trim()
        _filteredWords.value = _allWords.value.matchStart(
            q,
            sortOrder,
            { listOfNotNull(it.title, it.meaning, it.synonyms, it.conjugation, it.english) },
            { it.title },
            { it.liked })
        _filteredIdioms.value = _allIdioms.value.matchStart(
            q, sortOrder,
            { listOfNotNull(it.title, it.meaning) }, { it.title }, { it.liked })
        _filteredProverbs.value = _allProverbs.value.matchStart(
            q,
            sortOrder,
            { listOfNotNull(it.title, it.meaning, it.synonyms, it.conjugation) },
            { it.title },
            { it.liked })
        _filteredSayings.value = _allSayings.value.matchStart(
            q, sortOrder,
            { listOfNotNull(it.title, it.meaning) }, { it.title }, { it.liked })
    }

    fun searchMiddleOfTerms(query: String, sortOrder: SortOrder = SortOrder.AZ) {
        val q = query.lowercase().trim()
        _filteredWords.value = _allWords.value.matchContains(
            q,
            sortOrder,
            { listOfNotNull(it.title, it.meaning, it.synonyms, it.conjugation, it.english) },
            { it.title },
            { it.liked })
        _filteredIdioms.value = _allIdioms.value.matchContains(
            q, sortOrder,
            { listOfNotNull(it.title, it.meaning) }, { it.title }, { it.liked })
        _filteredProverbs.value = _allProverbs.value.matchContains(
            q,
            sortOrder,
            { listOfNotNull(it.title, it.meaning, it.synonyms, it.conjugation) },
            { it.title },
            { it.liked })
        _filteredSayings.value = _allSayings.value.matchContains(
            q, sortOrder,
            { listOfNotNull(it.title, it.meaning) }, { it.title }, { it.liked })
    }

    fun searchEndOfTerms(query: String, sortOrder: SortOrder = SortOrder.AZ) {
        val q = query.lowercase().trim()
        _filteredWords.value = _allWords.value.matchEnd(
            q,
            sortOrder,
            { listOfNotNull(it.title, it.meaning, it.synonyms, it.conjugation, it.english) },
            { it.title },
            { it.liked })
        _filteredIdioms.value = _allIdioms.value.matchEnd(
            q, sortOrder,
            { listOfNotNull(it.title, it.meaning) }, { it.title }, { it.liked })
        _filteredProverbs.value = _allProverbs.value.matchEnd(
            q,
            sortOrder,
            { listOfNotNull(it.title, it.meaning, it.synonyms, it.conjugation) },
            { it.title },
            { it.liked })
        _filteredSayings.value = _allSayings.value.matchEnd(
            q, sortOrder,
            { listOfNotNull(it.title, it.meaning) }, { it.title }, { it.liked })
    }

    fun likeWord(word: WordEntity) = viewModelScope.launch {
        val updated = word.copy(liked = !word.liked)
        wordRepo.updateWord(updated)
        _allWords.value = _allWords.value.replace(word.rid, updated)
        _filteredWords.value = _filteredWords.value.replace(word.rid, updated)
    }

    fun likeIdiom(idiom: IdiomEntity) = viewModelScope.launch {
        val updated = idiom.copy(liked = !idiom.liked)
        idiomRepo.updateIdiom(updated)
        _allIdioms.value = _allIdioms.value.replace(idiom.rid, updated)
        _filteredIdioms.value = _filteredIdioms.value.replace(idiom.rid, updated)
    }

    fun likeProverb(proverb: ProverbEntity) = viewModelScope.launch {
        val updated = proverb.copy(liked = !proverb.liked)
        proverbRepo.updateProverb(updated)
        _allProverbs.value = _allProverbs.value.replace(proverb.rid, updated)
        _filteredProverbs.value = _filteredProverbs.value.replace(proverb.rid, updated)
    }

    fun likeSaying(saying: SayingEntity) = viewModelScope.launch {
        val updated = saying.copy(liked = !saying.liked)
        sayingRepo.updateSaying(updated)
        _allSayings.value = _allSayings.value.replace(saying.rid, updated)
        _filteredSayings.value = _filteredSayings.value.replace(saying.rid, updated)
    }

    fun addToHistory(itemId: Int, type: String) = viewModelScope.launch {
        historyRepo.saveHistory(
            HistoryEntity(
                item = itemId,
                type = type,
                createdAt = System.currentTimeMillis().toString()
            )
        )
    }

    private fun <T> List<T>.replace(rid: Int, updated: T): List<T>
        where T : Any = map {
        if ((it as? WordEntity)?.rid == rid ||
            (it as? IdiomEntity)?.rid == rid ||
            (it as? ProverbEntity)?.rid == rid ||
            (it as? SayingEntity)?.rid == rid
        ) updated else it
    }

    private fun <T> List<T>.matchStart(
        q: String, order: SortOrder,
        fields: (T) -> List<String>, getTitle: (T) -> String?, getLiked: (T) -> Boolean,
    ) = (if (q.isEmpty()) this else filter { fields(it).any { f -> f.lowercase().startsWith(q) } })
        .applySortOrder(order, getTitle, getLiked)

    private fun <T> List<T>.matchContains(
        q: String, order: SortOrder,
        fields: (T) -> List<String>, getTitle: (T) -> String?, getLiked: (T) -> Boolean,
    ) = (if (q.isEmpty()) this else filter { fields(it).any { f -> f.lowercase().contains(q) } })
        .applySortOrder(order, getTitle, getLiked)

    private fun <T> List<T>.matchEnd(
        q: String, order: SortOrder,
        fields: (T) -> List<String>, getTitle: (T) -> String?, getLiked: (T) -> Boolean,
    ) = (if (q.isEmpty()) this else filter { fields(it).any { f -> f.lowercase().endsWith(q) } })
        .applySortOrder(order, getTitle, getLiked)

    private fun <T> List<T>.applySortOrder(
        order: SortOrder, getTitle: (T) -> String?, getLiked: (T) -> Boolean,
    ): List<T> = when (order) {
        SortOrder.AZ -> sortedBy { getTitle(it)?.lowercase() }
        SortOrder.ZA -> sortedByDescending { getTitle(it)?.lowercase() }
        SortOrder.LIKED_FIRST -> sortedWith(
            compareByDescending<T> { getLiked(it) }.thenBy { getTitle(it)?.lowercase() }
        )
    }
}