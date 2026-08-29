package com.swahilib.feature.home.viewmodel

import com.swahilib.core.data.repos.content.DailyContentRepo
import com.swahilib.core.data.repos.utils.PrefsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Owns today's word/proverb preview plus the current streak, used by the
 * once-a-day highlights dialog on [com.swahilib.feature.home.view.screen.HomeScreen].
 */
class DailyHighlightsController(
    private val dailyContentRepo: DailyContentRepo,
    private val prefsRepo: PrefsRepo,
) {
    private val _dailyHighlights = MutableStateFlow(HomeViewModel.DailyHighlights())
    val dailyHighlights: StateFlow<HomeViewModel.DailyHighlights> get() = _dailyHighlights

    /**
     * Loads today's word + proverb (same cached-per-day row the notifications,
     * widget, and Daily Word/Proverb screens all read) along with the current
     * streak. Suspend (not launched internally) so the caller can await it
     * before deciding whether to pop the once-a-day highlights dialog.
     */
    suspend fun load() {
        val (word, wordMeaning) = dailyContentRepo.getDailyWord()
        val (proverb, proverbMeaning) = dailyContentRepo.getDailyProverb()
        _dailyHighlights.value = HomeViewModel.DailyHighlights(
            word = word,
            wordMeaning = wordMeaning,
            proverb = proverb,
            proverbMeaning = proverbMeaning,
            streak = prefsRepo.currentStreak,
        )
    }
}
