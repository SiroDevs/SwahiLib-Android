package com.swahilib.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.DailyContentRepo
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.data.repos.HistoryRepo
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.SearchRepo
import com.swahilib.core.data.repos.WordRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataSettingsViewModel @Inject constructor(
    private val historyRepo: HistoryRepo,
    private val searchRepo: SearchRepo,
    private val wordRepo: WordRepo,
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val dailyContentRepo: DailyContentRepo,
    private val engagementRepo: EngagementRepo,
) : ViewModel() {

    /** "Futa Historia" - clears both reading (views) and search-text history. */
    fun clearHistory() {
        viewModelScope.launch {
            historyRepo.clearAll()
            searchRepo.clearAll()
        }
    }

    /** "Futa Vipendwa" - unlikes every word/idiom/proverb/saying. */
    fun clearLikes() {
        viewModelScope.launch {
            wordRepo.clearAllLikes()
            idiomRepo.clearAllLikes()
            proverbRepo.clearAllLikes()
            sayingRepo.clearAllLikes()
        }
    }

    /** "Futa Neno na Methali ya Kila Siku" - clears the daily-content selection history only
     * (does not touch the word/proverb dictionary itself). */
    fun clearDailyContent() {
        viewModelScope.launch {
            dailyContentRepo.clearAll()
        }
    }

    /** "Futa ChemshaBongo" - clears XP, streaks, challenges, achievements and stats. */
    fun clearEngagement() {
        viewModelScope.launch {
            engagementRepo.clearAllEngagementData()
        }
    }

    /** "Futa Kila Kitu" - runs all four of the above together. */
    fun clearEverything() {
        viewModelScope.launch {
            historyRepo.clearAll()
            searchRepo.clearAll()
            wordRepo.clearAllLikes()
            idiomRepo.clearAllLikes()
            proverbRepo.clearAllLikes()
            sayingRepo.clearAllLikes()
            dailyContentRepo.clearAll()
            engagementRepo.clearAllEngagementData()
        }
    }
}
