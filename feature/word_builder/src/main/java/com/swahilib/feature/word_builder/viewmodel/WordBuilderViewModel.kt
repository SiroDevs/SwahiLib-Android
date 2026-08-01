package com.swahilib.feature.wordbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.WordBuilderScorer
import com.swahilib.core.games.generator.WordBuilderGenerator
import com.swahilib.core.games.model.ScrambledWord
import com.swahilib.core.games.model.WordBuilderRoundResult
import com.swahilib.core.games.model.WordBuilderSessionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ROUND_TIME_SECONDS = 30

sealed interface WordBuilderUiState {
    data object Loading : WordBuilderUiState
    data object Empty : WordBuilderUiState
    data class Playing(
        val word: ScrambledWord,
        val roundIndex: Int,
        val totalRounds: Int?, // null in endless mode
        val pickedIndices: List<Int> = emptyList(),
        val revealedCount: Int = 0, // letters auto-placed by hints, from the start of the word
        val hintsUsed: Int = 0,
        val feedback: Boolean? = null, // null = unanswered, true/false = last round outcome
        val secondsRemaining: Int? = null,
        val endless: Boolean = false,
    ) : WordBuilderUiState {
        val assembled: String get() = pickedIndices.joinToString("") { word.scrambledLetters[it].toString() }
    }
    data class Finished(val result: WordBuilderSessionResult) : WordBuilderUiState
}

@HiltViewModel
class WordBuilderViewModel @Inject constructor(
    private val generator: WordBuilderGenerator,
    private val engagementRepo: EngagementRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WordBuilderUiState>(WordBuilderUiState.Loading)
    val uiState: StateFlow<WordBuilderUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var timedMode: Boolean = false
    private var endless: Boolean = false
    private var startedAtMs: Long = 0L
    private var rounds = mutableListOf<WordBuilderRoundResult>()
    private var usedRids = mutableSetOf<Int>()
    private var timerJob: Job? = null

    fun start(
        challengeId: String?,
        activityId: String?,
        difficulty: Difficulty = Difficulty.BEGINNER,
        wordCount: Int = 5,
        timedMode: Boolean = false,
        endless: Boolean = false,
    ) {
        if (_uiState.value !is WordBuilderUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty
        this.timedMode = timedMode
        this.endless = endless
        startedAtMs = System.currentTimeMillis()

        viewModelScope.launch {
            if (endless) {
                val first = generator.next(difficulty, usedRids)
                _uiState.value = first?.let { toPlaying(it, 0, null) } ?: WordBuilderUiState.Empty
                first?.let { usedRids.add(it.sourceWordRid) }
            } else {
                val session = generator.session(difficulty, wordCount)
                _uiState.value = session.firstOrNull()?.let { toPlaying(it, 0, session.size) } ?: WordBuilderUiState.Empty
                session.firstOrNull()?.let { usedRids.add(it.sourceWordRid) }
                pendingSession = session
            }
            maybeStartTimer()
        }
    }

    private var pendingSession: List<ScrambledWord> = emptyList()

    private fun toPlaying(word: ScrambledWord, roundIndex: Int, total: Int?) = WordBuilderUiState.Playing(
        word = word,
        roundIndex = roundIndex,
        totalRounds = total,
        endless = endless,
        secondsRemaining = if (timedMode) ROUND_TIME_SECONDS else null,
    )

    fun pickLetter(index: Int) {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.feedback != null || index in state.pickedIndices) return
        _uiState.value = state.copy(pickedIndices = state.pickedIndices + index)
    }

    fun clearPicks() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.feedback != null) return
        _uiState.value = state.copy(pickedIndices = state.pickedIndices.take(state.revealedCount))
    }

    /** Reveals the next correct letter, auto-placing it and consuming a hint. */
    fun useHint() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.feedback != null || state.revealedCount >= state.word.answer.length) return
        // Find (and place) the tile matching the next needed letter that isn't already picked.
        val nextChar = state.word.answer[state.revealedCount]
        val tileIndex = state.word.scrambledLetters.indices
            .firstOrNull { it !in state.pickedIndices && state.word.scrambledLetters[it] == nextChar }
            ?: return
        _uiState.value = state.copy(
            pickedIndices = state.pickedIndices + tileIndex,
            revealedCount = state.revealedCount + 1,
            hintsUsed = state.hintsUsed + 1,
        )
    }

    fun submit() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.feedback != null) return
        timerJob?.cancel()
        recordRound(state, gaveUp = false)
    }

    private fun recordRound(state: WordBuilderUiState.Playing, gaveUp: Boolean) {
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(0)
        val result = if (gaveUp) {
            WordBuilderRoundResult(state.word.id, correct = false, hintsUsed = state.hintsUsed, secondsSpent = secondsSpent, gaveUp = true)
        } else {
            WordBuilderScorer.checkAnswer(state.word, state.assembled, state.hintsUsed, secondsSpent)
        }
        rounds.add(result)
        _uiState.value = state.copy(feedback = result.correct)
    }

    fun next() {
        viewModelScope.launch {
            val state = _uiState.value as? WordBuilderUiState.Playing ?: return@launch
            if (state.feedback == null) return@launch

            if (endless) {
                val nextWord = generator.next(difficulty, usedRids)
                if (nextWord == null) {
                    finish()
                } else {
                    usedRids.add(nextWord.sourceWordRid)
                    _uiState.value = toPlaying(nextWord, state.roundIndex + 1, null)
                    maybeStartTimer()
                }
            } else {
                val nextIndex = state.roundIndex + 1
                if (nextIndex >= pendingSession.size) {
                    finish()
                } else {
                    _uiState.value = toPlaying(pendingSession[nextIndex], nextIndex, pendingSession.size)
                    maybeStartTimer()
                }
            }
        }
    }

    /** Endless mode: user taps "Maliza" to stop and bank their XP. */
    fun stopEndless() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.feedback == null) recordRound(state, gaveUp = true)
        finish()
    }

    private fun maybeStartTimer() {
        timerJob?.cancel()
        if (!timedMode) return
        timerJob = viewModelScope.launch {
            var remaining = ROUND_TIME_SECONDS
            while (isActive && remaining > 0) {
                delay(1000)
                remaining--
                val s = _uiState.value as? WordBuilderUiState.Playing ?: return@launch
                if (s.feedback != null) return@launch
                _uiState.value = s.copy(secondsRemaining = remaining)
            }
            val s = _uiState.value as? WordBuilderUiState.Playing ?: return@launch
            if (s.feedback == null) recordRound(s, gaveUp = true)
        }
    }

    private fun finish() {
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val result = WordBuilderScorer.tally(rounds, difficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engagementRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.WORD_BUILDER, difficulty)
            } else if (result.xpEarned > 0) {
                engagementRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.WORD_BUILDER,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            engagementRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.WORD_BUILDER,
                title = "Jenzi la Maneno",
                score = result.correctWords,
                maxScore = result.totalWords,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            _uiState.value = WordBuilderUiState.Finished(result.copy(xpEarned = xpEarnedThisSession))
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
