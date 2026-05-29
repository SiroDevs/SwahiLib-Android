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

    fun setSelectedTab(tab: HomeTab) {
        _selectedTab.value = tab
    }

    fun fetchData() {
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

            _history.value = historyRepo.fetchLocalData().sortedByDescending { it.createdAt }
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun filterData(query: String) {
        val q = query.lowercase()
        _filteredIdioms.value = if (q.isEmpty()) _allIdioms.value
        else _allIdioms.value.filter { it.title?.lowercase()?.startsWith(q) == true }
        _filteredProverbs.value = if (q.isEmpty()) _allProverbs.value
        else _allProverbs.value.filter { it.title?.lowercase()?.startsWith(q) == true }
        _filteredSayings.value = if (q.isEmpty()) _allSayings.value
        else _allSayings.value.filter { it.title?.lowercase()?.startsWith(q) == true }
        _filteredWords.value = if (q.isEmpty()) _allWords.value
        else _allWords.value.filter { it.title?.lowercase()?.startsWith(q) == true }
    }

    fun likeWord(word: WordEntity) {
        viewModelScope.launch {
            val updated = word.copy(liked = !word.liked)
            wordRepo.updateWord(updated)
            _allWords.value = _allWords.value.map { if (it.rid == word.rid) updated else it }
            _filteredWords.value =
                _filteredWords.value.map { if (it.rid == word.rid) updated else it }
        }
    }

    fun likeIdiom(idiom: IdiomEntity) {
        viewModelScope.launch {
            val updated = idiom.copy(liked = !idiom.liked)
            idiomRepo.updateIdiom(updated)
            _allIdioms.value = _allIdioms.value.map { if (it.rid == idiom.rid) updated else it }
            _filteredIdioms.value =
                _filteredIdioms.value.map { if (it.rid == idiom.rid) updated else it }
        }
    }

    fun likeProverb(proverb: ProverbEntity) {
        viewModelScope.launch {
            val updated = proverb.copy(liked = !proverb.liked)
            proverbRepo.updateProverb(updated)
            _allProverbs.value =
                _allProverbs.value.map { if (it.rid == proverb.rid) updated else it }
            _filteredProverbs.value =
                _filteredProverbs.value.map { if (it.rid == proverb.rid) updated else it }
        }
    }

    fun likeSaying(saying: SayingEntity) {
        viewModelScope.launch {
            val updated = saying.copy(liked = !saying.liked)
            sayingRepo.updateSaying(updated)
            _allSayings.value = _allSayings.value.map { if (it.rid == saying.rid) updated else it }
            _filteredSayings.value =
                _filteredSayings.value.map { if (it.rid == saying.rid) updated else it }
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
            "word" -> _allWords.value.find { it.rid == historyEntity.item }
                ?.let { ContentItem.Word(it) }

            "idiom" -> _allIdioms.value.find { it.rid == historyEntity.item }
                ?.let { ContentItem.Idiom(it) }

            "proverb" -> _allProverbs.value.find { it.rid == historyEntity.item }
                ?.let { ContentItem.Proverb(it) }

            "saying" -> _allSayings.value.find { it.rid == historyEntity.item }
                ?.let { ContentItem.Saying(it) }

            else -> null
        }
    }
}