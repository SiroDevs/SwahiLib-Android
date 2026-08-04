package com.swahilib.feature.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.data.repos.DailyContentRepo
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.model.HistoryEntity
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.SearchEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.feature.home.view.components.ContentItem
import com.swahilib.feature.home.view.components.HomeTab
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    idiomRepo: IdiomRepo,
    proverbRepo: ProverbRepo,
    sayingRepo: SayingRepo,
    wordRepo: WordRepo,
    historyRepo: HistoryRepo,
    searchRepo: SearchRepo,
    dailyContentRepo: DailyContentRepo,
    private val prefsRepo: PrefsRepo,
    @ApplicationContext context: Context,
) : ViewModel() {

    enum class HistorySubTab { USOMAJI, UTAFUTAJI }

    /** Word/proverb-of-the-day plus current streak, for the daily highlights dialog. */
    data class DailyHighlights(
        val word: WordEntity? = null,
        val wordMeaning: String = "",
        val proverb: ProverbEntity? = null,
        val proverbMeaning: String = "",
        val streak: Int = 0,
    )

    private val content = ContentController(
        idiomRepo = idiomRepo,
        proverbRepo = proverbRepo,
        sayingRepo = sayingRepo,
        wordRepo = wordRepo,
        prefsRepo = prefsRepo,
        context = context,
        scope = viewModelScope,
    )
    private val dailyHighlightsController = DailyHighlightsController(dailyContentRepo, prefsRepo)
    private val readingHistory = ReadingHistoryController(historyRepo, viewModelScope)
    private val searchHistoryController = SearchHistoryController(searchRepo, viewModelScope)
    private val tabController = TabController()

    val uiState: StateFlow<UiState> get() = content.uiState
    val filteredIdioms: StateFlow<List<IdiomEntity>> get() = content.filteredIdioms
    val filteredProverbs: StateFlow<List<ProverbEntity>> get() = content.filteredProverbs
    val filteredSayings: StateFlow<List<SayingEntity>> get() = content.filteredSayings
    val filteredWords: StateFlow<List<WordEntity>> get() = content.filteredWords
    val likedWords: StateFlow<List<WordEntity>> get() = content.likedWords
    val likedIdioms: StateFlow<List<IdiomEntity>> get() = content.likedIdioms
    val likedProverbs: StateFlow<List<ProverbEntity>> get() = content.likedProverbs
    val likedSayings: StateFlow<List<SayingEntity>> get() = content.likedSayings

    fun fetchData(force: Boolean = false) {
        content.fetchData(force)
        readingHistory.refreshHistory()
    }

    fun filterData(query: String) {
        content.filterData(query)
        if (query.isBlank()) {
            searchHistoryController.cancelTracking()
        } else {
            searchHistoryController.trackSearch(query)
        }
    }

    fun likeWord(word: WordEntity) = content.likeWord(word)
    fun likeIdiom(idiom: IdiomEntity) = content.likeIdiom(idiom)
    fun likeProverb(proverb: ProverbEntity) = content.likeProverb(proverb)
    fun likeSaying(saying: SayingEntity) = content.likeSaying(saying)
    fun clearAllLikes() = content.clearAllLikes()

    // ── Reading history → ReadingHistoryController ──
    val history: StateFlow<List<HistoryEntity>> get() = readingHistory.history
    fun refreshHistory() = readingHistory.refreshHistory()
    fun clearReadingHistory() = readingHistory.clearReadingHistory()
    fun addToHistory(itemId: Int, type: String) = readingHistory.addToHistory(itemId, type)

    /** Resolves a reading-history row back to the underlying content item, for display. */
    fun resolveHistoryItem(historyEntity: HistoryEntity): ContentItem? =
        content.findContentItem(historyEntity.type, historyEntity.item)

    // ── Search history → SearchHistoryController ──
    val searchHistory: StateFlow<List<SearchEntity>> get() = searchHistoryController.searchHistory
    val reviewSuggestions: StateFlow<List<SearchEntity>> get() = searchHistoryController.reviewSuggestions
    val pendingSearchQuery: StateFlow<String?> get() = searchHistoryController.pendingSearchQuery
    fun consumePendingSearchQuery() = searchHistoryController.consumePendingSearchQuery()
    fun refreshSearchHistory() = searchHistoryController.refreshSearchHistory()
    fun clearSearchHistory() = searchHistoryController.clearSearchHistory()

    /** Switches to the Search tab and pre-fills [query], e.g. from a tapped search-history row. */
    fun requestSearch(query: String) {
        searchHistoryController.setPendingQuery(query)
        tabController.setSelectedTab(HomeTab.Search)
    }

    // ── Tabs → TabController ──
    val selectedTab: StateFlow<HomeTab> get() = tabController.selectedTab
    fun setSelectedTab(tab: HomeTab) = tabController.setSelectedTab(tab)
    val historySubTab: StateFlow<HistorySubTab> get() = tabController.historySubTab
    fun setHistorySubTab(tab: HistorySubTab) = tabController.setHistorySubTab(tab)

    // ── Daily highlights → DailyHighlightsController ──
    val dailyHighlights: StateFlow<DailyHighlights> get() = dailyHighlightsController.dailyHighlights

    /** See [DailyHighlightsController.load] - suspend so callers can await it before showing the dialog. */
    suspend fun loadDailyHighlights() = dailyHighlightsController.load()
}
