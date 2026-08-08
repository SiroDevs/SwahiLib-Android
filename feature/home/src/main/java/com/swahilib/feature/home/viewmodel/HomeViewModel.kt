package com.swahilib.feature.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.data.repos.DailyContentRepo
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.engagement.engine.ActivityRecommendation
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.Challenge
import com.swahilib.core.engagement.model.StatisticsSummary
import com.swahilib.core.engagement.model.UserProgress
import com.swahilib.feature.home.view.components.HomeTab
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val engageRepo: EngagementRepo,
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

    private val _progress = MutableStateFlow<UserProgress?>(null)
    val progress: StateFlow<UserProgress?> = _progress.asStateFlow()

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges.asStateFlow()

    private val _stats = MutableStateFlow<StatisticsSummary?>(null)
    val stats: StateFlow<StatisticsSummary?> = _stats.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _recommendations = MutableStateFlow<List<ActivityRecommendation>>(emptyList())
    val recommendations: StateFlow<List<ActivityRecommendation>> = _recommendations.asStateFlow()

    fun fetchData(force: Boolean = false) = content.fetchData(force)

    fun filterData(query: String) {
        content.filterData(query)
        if (query.isBlank()) {
            searchHistoryController.cancelTracking()
        } else {
            searchHistoryController.trackSearch(query)
        }
    }
    
    fun refreshProgress() {
        viewModelScope.launch {
            _progress.value = engageRepo.currentProgress()
            _challenges.value = engageRepo.activeChallenges()
            _stats.value = engageRepo.statistics()
            _achievements.value = engageRepo.achievementsWithStatus()
            _recommendations.value = engageRepo.recommendedActivities()
        }
    }

    fun completeActivity(challengeId: String, activityId: String, secondsSpent: Int = 0) {
        viewModelScope.launch {
            engageRepo.markActivityComplete(challengeId, activityId, secondsSpent)
            refreshProgress()
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
