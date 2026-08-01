package com.swahilib.feature.wordsearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.WordSearchScorer
import com.swahilib.core.games.generator.WordSearchGenerator
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.WordSearchPuzzle
import com.swahilib.core.games.model.WordSearchResult
import com.swahilib.core.games.model.WordSearchTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WordSearchUiState {
    data object Loading : WordSearchUiState
    data object Empty : WordSearchUiState
    data class Playing(
        val puzzle: WordSearchPuzzle,
        val words: List<PlacedWord>,
        val selectionStart: Pair<Int, Int>? = null,
        val lastMissed: Boolean = false,
    ) : WordSearchUiState
    data class Finished(val result: WordSearchResult) : WordSearchUiState
}

@HiltViewModel
class WordSearchViewModel @Inject constructor(
    private val generator: WordSearchGenerator,
    private val engagementRepo: EngagementRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WordSearchUiState>(WordSearchUiState.Loading)
    val uiState: StateFlow<WordSearchUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L

    fun start(
        challengeId: String?,
        activityId: String?,
        difficulty: Difficulty = Difficulty.BEGINNER,
        theme: WordSearchTheme = WordSearchTheme.RANDOM,
    ) {
        if (_uiState.value !is WordSearchUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            val puzzle = generator.generate(difficulty = difficulty, theme = theme)
            _uiState.value = if (puzzle.words.size < 2) {
                WordSearchUiState.Empty
            } else {
                WordSearchUiState.Playing(puzzle = puzzle, words = puzzle.words)
            }
        }
    }

    fun tapCell(row: Int, col: Int) {
        val state = _uiState.value as? WordSearchUiState.Playing ?: return
        val start = state.selectionStart

        if (start == null) {
            _uiState.value = state.copy(selectionStart = row to col, lastMissed = false)
            return
        }

        val match = WordSearchScorer.matchSelection(state.words, start.first, start.second, row, col)
        if (match != null) {
            val updatedWords = state.words.map { if (it.word == match.word) it.copy(found = true) else it }
            _uiState.value = state.copy(words = updatedWords, selectionStart = null, lastMissed = false)
            if (updatedWords.all { it.found }) finish(updatedWords)
        } else {
            _uiState.value = state.copy(selectionStart = null, lastMissed = true)
        }
    }

    /** User gives up before finding every word. */
    fun giveUp() {
        val state = _uiState.value as? WordSearchUiState.Playing ?: return
        finish(state.words)
    }

    private fun finish(words: List<PlacedWord>) {
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = WordSearchScorer.tally(words, difficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engagementRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.WORD_SEARCH, difficulty)
            } else if (result.xpEarned > 0) {
                engagementRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.WORD_SEARCH,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            engagementRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.WORD_SEARCH,
                title = "Tafuta Maneno",
                score = result.foundWords,
                maxScore = result.totalWords,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            _uiState.value = WordSearchUiState.Finished(result.copy(xpEarned = xpEarnedThisSession))
        }
    }
}
