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
import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.database.entities.content.WordEntity
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

    fun fetchData(force: Boolean = false) = content.fetchData(force)

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

    /** Records a viewed item. Display/clearing of this history now lives in feature:history. */
    fun addToHistory(itemId: Int, type: String) = readingHistory.addToHistory(itemId, type)

    val pendingSearchQuery: StateFlow<String?> get() = searchHistoryController.pendingSearchQuery
    fun consumePendingSearchQuery() = searchHistoryController.consumePendingSearchQuery()

    /** Switches to the Search tab and pre-fills [query] - used when Home picks up a tapped row from History. */
    fun requestSearch(query: String) {
        searchHistoryController.setPendingQuery(query)
        tabController.setSelectedTab(HomeTab.Search)
    }

    // ── Tabs → TabController ──
    val selectedTab: StateFlow<HomeTab> get() = tabController.selectedTab
    fun setSelectedTab(tab: HomeTab) = tabController.setSelectedTab(tab)

    // ── Daily highlights → DailyHighlightsController ──
    val dailyHighlights: StateFlow<DailyHighlights> get() = dailyHighlightsController.dailyHighlights

    /** See [DailyHighlightsController.load] - suspend so callers can await it before showing the dialog. */
    suspend fun loadDailyHighlights() = dailyHighlightsController.load()
}
