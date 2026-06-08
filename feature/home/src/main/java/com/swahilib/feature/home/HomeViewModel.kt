package com.swahilib.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.model.HistoryEntity
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.feature.home.components.ContentItem
import com.swahilib.feature.home.components.HomeTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
    private val historyRepo: HistoryRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _allIdioms    = MutableStateFlow<List<IdiomEntity>>(emptyList())
    private val _allProverbs  = MutableStateFlow<List<ProverbEntity>>(emptyList())
    private val _allSayings   = MutableStateFlow<List<SayingEntity>>(emptyList())
    private val _allWords     = MutableStateFlow<List<WordEntity>>(emptyList())

    private val _filteredIdioms   = MutableStateFlow<List<IdiomEntity>>(emptyList())
    private val _filteredProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    private val _filteredSayings  = MutableStateFlow<List<SayingEntity>>(emptyList())
    private val _filteredWords    = MutableStateFlow<List<WordEntity>>(emptyList())
    val filteredIdioms:   StateFlow<List<IdiomEntity>>   get() = _filteredIdioms
    val filteredProverbs: StateFlow<List<ProverbEntity>> get() = _filteredProverbs
    val filteredSayings:  StateFlow<List<SayingEntity>>  get() = _filteredSayings
    val filteredWords:    StateFlow<List<WordEntity>>    get() = _filteredWords

    val likedWords: StateFlow<List<WordEntity>>
        get() = _allWords.map { it.filter { w -> w.liked } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val likedIdioms: StateFlow<List<IdiomEntity>>
        get() = _allIdioms.map { it.filter { i -> i.liked } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val likedProverbs: StateFlow<List<ProverbEntity>>
        get() = _allProverbs.map { it.filter { p -> p.liked } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val likedSayings: StateFlow<List<SayingEntity>>
        get() = _allSayings.map { it.filter { s -> s.liked } }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _history = MutableStateFlow<List<HistoryEntity>>(emptyList())
    val history: StateFlow<List<HistoryEntity>> get() = _history

    private val _selectedTab = MutableStateFlow<HomeTab>(HomeTab.Search)
    val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()

    fun setSelectedTab(tab: HomeTab) { _selectedTab.value = tab }

    fun fetchData(force: Boolean = false) {
        if (!force && _uiState.value is UiState.Filtered) return
        _uiState.tryEmit(UiState.Loading)
        viewModelScope.launch {
            _allIdioms.value   = idiomRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredIdioms.value = _allIdioms.value

            _allProverbs.value = proverbRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredProverbs.value = _allProverbs.value

            _allSayings.value  = sayingRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredSayings.value = _allSayings.value

            _allWords.value    = wordRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
            _filteredWords.value = _allWords.value

            _history.value = historyRepo.fetchLocalData().sortedByDescending { it.createdAt }
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun refreshHistory() {
        viewModelScope.launch {
            _history.value = historyRepo.fetchLocalData().sortedByDescending { it.createdAt }
        }
    }

    fun filterData(query: String) {
        val q = query.lowercase().trim()
        if (q.isEmpty()) {
            _filteredIdioms.value   = _allIdioms.value
            _filteredProverbs.value = _allProverbs.value
            _filteredSayings.value  = _allSayings.value
            _filteredWords.value    = _allWords.value
            return
        }

        _filteredIdioms.value   = fuzzyRank(_allIdioms.value,   q) { it.title }
        _filteredProverbs.value = fuzzyRank(_allProverbs.value, q) { it.title }
        _filteredSayings.value  = fuzzyRank(_allSayings.value,  q) { it.title }
        _filteredWords.value    = fuzzyRank(_allWords.value,    q) { it.title }
    }

    /**
     * Ranks items by relevance to [query]:
     *  0 – exact match
     *  1 – starts with query
     *  2 – contains query
     *  3 – fuzzy (edit-distance ≤ max(1, query.length/3))
     *
     * Items with no match at all are excluded from results.
     */
    private fun <T> fuzzyRank(items: List<T>, query: String, title: (T) -> String?): List<T> {
        val maxDist = max(1, query.length / 3)
        return items
            .mapNotNull { item ->
                val t = title(item)?.lowercase() ?: return@mapNotNull null
                val priority = when {
                    t == query                    -> 0
                    t.startsWith(query)           -> 1
                    t.contains(query)             -> 2
                    editDistance(t, query) <= maxDist -> 3
                    else                          -> return@mapNotNull null
                }
                item to priority
            }
            .sortedWith(compareBy({ it.second }, { title(it.first)?.lowercase() }))
            .map { it.first }
    }

    /**
     * Classic Levenshtein edit-distance (capped early for performance).
     */
    private fun editDistance(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        // Only compute on first window to keep it O(n) space
        val s = if (a.length <= b.length) a else b
        val t = if (a.length <= b.length) b else a
        var prev = IntArray(s.length + 1) { it }
        for (j in 1..t.length) {
            val curr = IntArray(s.length + 1)
            curr[0] = j
            for (i in 1..s.length) {
                curr[i] = if (s[i - 1] == t[j - 1]) prev[i - 1]
                else 1 + minOf(prev[i], curr[i - 1], prev[i - 1])
            }
            prev = curr
        }
        return prev[s.length]
    }

    fun likeWord(word: WordEntity) {
        viewModelScope.launch {
            val updated = word.copy(liked = !word.liked)
            wordRepo.updateWord(updated)
            _allWords.value = _allWords.value.map { if (it.rid == word.rid) updated else it }
            _filteredWords.value = _filteredWords.value.map { if (it.rid == word.rid) updated else it }
        }
    }

    fun likeIdiom(idiom: IdiomEntity) {
        viewModelScope.launch {
            val updated = idiom.copy(liked = !idiom.liked)
            idiomRepo.updateIdiom(updated)
            _allIdioms.value = _allIdioms.value.map { if (it.rid == idiom.rid) updated else it }
            _filteredIdioms.value = _filteredIdioms.value.map { if (it.rid == idiom.rid) updated else it }
        }
    }

    fun likeProverb(proverb: ProverbEntity) {
        viewModelScope.launch {
            val updated = proverb.copy(liked = !proverb.liked)
            proverbRepo.updateProverb(updated)
            _allProverbs.value = _allProverbs.value.map { if (it.rid == proverb.rid) updated else it }
            _filteredProverbs.value = _filteredProverbs.value.map { if (it.rid == proverb.rid) updated else it }
        }
    }

    fun likeSaying(saying: SayingEntity) {
        viewModelScope.launch {
            val updated = saying.copy(liked = !saying.liked)
            sayingRepo.updateSaying(updated)
            _allSayings.value = _allSayings.value.map { if (it.rid == saying.rid) updated else it }
            _filteredSayings.value = _filteredSayings.value.map { if (it.rid == saying.rid) updated else it }
        }
    }

    fun addToHistory(itemId: Int, type: String) {
        viewModelScope.launch {
            val entry = HistoryEntity(
                item = itemId,
                type = type,
                createdAt = System.currentTimeMillis().toString()
            )
            historyRepo.saveHistory(entry)
            _history.value = listOf(entry) + _history.value
        }
    }

    fun resolveHistoryItem(historyEntity: HistoryEntity): ContentItem? {
        return when (historyEntity.type) {
            "word"    -> _allWords.value.find { it.rid == historyEntity.item }?.let { ContentItem.Word(it) }
            "idiom"   -> _allIdioms.value.find { it.rid == historyEntity.item }?.let { ContentItem.Idiom(it) }
            "proverb" -> _allProverbs.value.find { it.rid == historyEntity.item }?.let { ContentItem.Proverb(it) }
            "saying"  -> _allSayings.value.find { it.rid == historyEntity.item }?.let { ContentItem.Saying(it) }
            else      -> null
        }
    }
}
