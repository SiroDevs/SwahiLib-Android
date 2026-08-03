package com.swahilib.feature.daily_content.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.DailyContentHistoryEntry
import com.swahilib.core.data.repos.DailyContentRepo
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.WordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyContentViewModel @Inject constructor(
    private val dailyContentRepo: DailyContentRepo,
) : ViewModel() {

    suspend fun getDailyWord(): Pair<WordEntity?, String> = dailyContentRepo.getDailyWord()

    suspend fun getDailyProverb(): Pair<ProverbEntity?, String> = dailyContentRepo.getDailyProverb()

    private val _history = MutableStateFlow<List<DailyContentHistoryEntry>>(emptyList())
    val history: StateFlow<List<DailyContentHistoryEntry>> get() = _history

    private var historyLoaded = false

    /** Loads the full daily-content history once; safe to call from every entry point. */
    fun loadHistory() {
        if (historyLoaded) return
        historyLoaded = true
        viewModelScope.launch { _history.value = dailyContentRepo.getHistory() }
    }
}
