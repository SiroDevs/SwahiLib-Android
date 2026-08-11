package com.swahilib.feature.word_search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.data.repos.GameProgressRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.Achievement
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.GameLevelConfig
import com.swahilib.core.games.engine.GameStepTimer
import com.swahilib.core.games.engine.WordSearchScorer
import com.swahilib.core.games.generator.WordSearchGenerator
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.WordSearchPuzzle
import com.swahilib.core.games.model.WordSearchResult
import com.swahilib.core.games.model.WordSearchTheme
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.random.Random

sealed interface WordSearchUiState {
    data object Loading : WordSearchUiState
    data object Empty : WordSearchUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : WordSearchUiState

    data class Playing(
        val puzzle: WordSearchPuzzle,
        val words: List<PlacedWord>,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val selectionStart: Pair<Int, Int>? = null,
        val lastMissed: Boolean = false,
        val highlightedLetter: Char? = null,
        val secondsRemaining: Int,
        val secondsTotal: Int,
        val easyMode: Boolean,
    ) : WordSearchUiState {
        val letterPool: List<Char> get() = words.flatMap { it.word.toList() }.distinct().sorted()
    }

    data class Finished(
        val result: WordSearchResult,
        val words: List<PlacedWord>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
    ) : WordSearchUiState
}

@Serializable
private data class WordSearchSnapshot(val foundWords: List<String>)

@HiltViewModel
class WordSearchViewModel @Inject constructor(
    private val generator: WordSearchGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    private val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val gameType = StatisticsEngine.EventType.WORD_SEARCH.name

    private val _uiState = MutableStateFlow<WordSearchUiState>(WordSearchUiState.Loading)
    val uiState: StateFlow<WordSearchUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var theme: WordSearchTheme = WordSearchTheme.RANDOM
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L

    private val stepTimer = GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onTimeUp)

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
        this.theme = theme

        viewModelScope.launch {
            if (challengeId != null) {
                beginSession(level = null, resume = false)
            } else {
                showLevelSelect()
            }
        }
    }

    private suspend fun showLevelSelect() {
        val progress = gameProgressRepo.getProgress(gameType)
        val levels = GameLevelConfig.levels().map { lvl ->
            GameLevelUiModel(
                level = lvl,
                bannerPoints = GameLevelConfig.bannerPointsForLevel(lvl),
                unlocked = lvl <= progress.highestUnlockedLevel,
                isCurrent = lvl == progress.highestUnlockedLevel,
            )
        }
        _uiState.value = WordSearchUiState.LevelSelect(levels, progress.totalPoints.toInt())
    }

    fun chooseLevel(level: Int) {
        viewModelScope.launch {
            if (!gameProgressRepo.canPlay(gameType, level)) {
                soundPlayer.play(GameSound.LOCKED)
                return@launch
            }
            beginSession(level = level, resume = true)
        }
    }

    private suspend fun beginSession(level: Int?, resume: Boolean) {
        startedAtMs = System.currentTimeMillis()
        val progress = gameProgressRepo.getProgress(gameType)
        val effectiveDifficulty = level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val wordCount = level?.let { GameLevelConfig.stepCountForLevel(it) } ?: 8
        val easy = level?.let { GameLevelConfig.isEasyLevel(it) } ?: false

        val saved = if (resume) gameProgressRepo.loadSession(gameType) else null
        val matchesSaved = saved != null && saved.level == (level ?: 0)

        contentSeed = if (matchesSaved) saved!!.contentSeed else Random.nextLong()
        val puzzle = generator.generate(effectiveDifficulty, theme, wordCount, contentSeed, easyFillerPool = easy)
        if (puzzle.words.size < 2) {
            _uiState.value = WordSearchUiState.Empty
            return
        }

        var words = puzzle.words
        var livePoints = 0
        if (matchesSaved) {
            val snapshot = runCatching { Json.decodeFromString<WordSearchSnapshot>(saved!!.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                val foundSet = snapshot.foundWords.toSet()
                words = words.map { if (it.word in foundSet) it.copy(found = true) else it }
                livePoints = saved.livePoints
            }
        }

        val totalSeconds = level?.let { GameLevelConfig.timerSecondsForLevel(it) * 2 } ?: 180 // whole puzzle, not per-word
        _uiState.value = WordSearchUiState.Playing(
            puzzle = puzzle,
            words = words,
            level = level,
            previousPoints = progress.totalPoints.toInt(),
            livePoints = livePoints,
            secondsRemaining = totalSeconds,
            secondsTotal = totalSeconds,
            easyMode = easy,
        )
        stepTimer.start(totalSeconds)
    }

    private fun onTick(secondsRemaining: Int) {
        val state = _uiState.value as? WordSearchUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onTimeUp() {
        val state = _uiState.value as? WordSearchUiState.Playing ?: return
        soundPlayer.play(GameSound.TIME_UP)
        finish(state.words, state)
    }

    fun tapCell(row: Int, col: Int) {
        val state = _uiState.value as? WordSearchUiState.Playing ?: return
        val start = state.selectionStart
        soundPlayer.play(GameSound.TAP)

        if (start == null) {
            _uiState.value = state.copy(selectionStart = row to col, lastMissed = false)
            return
        }

        val match = WordSearchScorer.matchSelection(state.words, start.first, start.second, row, col)
        if (match != null) {
            onWordFound(state, match.word)
        } else {
            _uiState.value = state.copy(selectionStart = null, lastMissed = true)
        }
    }

    /** Easy-mode aid: tapping a pool letter flashes every occurrence of it in the grid to help scanning. */
    fun tapPoolLetter(letter: Char) {
        val state = _uiState.value as? WordSearchUiState.Playing ?: return
        soundPlayer.play(GameSound.TAP)
        _uiState.value = state.copy(highlightedLetter = letter)
    }

    private fun onWordFound(state: WordSearchUiState.Playing, foundWord: String) {
        val updatedWords = state.words.map { if (it.word == foundWord) it.copy(found = true) else it }
        val bonus = state.level?.let { GameLevelConfig.pointsPerCorrect(it) } ?: 10
        val updated = state.copy(
            words = updatedWords,
            selectionStart = null,
            lastMissed = false,
            livePoints = state.livePoints + bonus,
        )
        _uiState.value = updated
        soundPlayer.play(GameSound.SUBMIT)
        persistSnapshot(updated)
        if (updatedWords.all { it.found }) {
            soundPlayer.play(GameSound.LEVEL_COMPLETE)
            finish(updatedWords, updated)
        }
    }

    private fun persistSnapshot(state: WordSearchUiState.Playing) {
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = 0,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(
                    WordSearchSnapshot.serializer(),
                    WordSearchSnapshot(state.words.filter { it.found }.map { it.word }),
                ),
            )
        }
    }

    /** User gives up before finding every word. */
    fun giveUp() {
        val state = _uiState.value as? WordSearchUiState.Playing ?: return
        finish(state.words, state)
    }

    fun restart() {
        val level = (_uiState.value as? WordSearchUiState.Playing)?.level
        stepTimer.stop()
        _uiState.value = WordSearchUiState.Loading
        viewModelScope.launch {
            gameProgressRepo.clearSession(gameType)
            beginSession(level = level, resume = false)
        }
    }

    fun discardAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        viewModelScope.launch {
            gameProgressRepo.clearSession(gameType)
            onDone()
        }
    }

    fun saveAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        val state = _uiState.value as? WordSearchUiState.Playing
        if (state == null) {
            onDone()
            return
        }
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = 0,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(
                    WordSearchSnapshot.serializer(),
                    WordSearchSnapshot(state.words.filter { it.found }.map { it.word }),
                ),
            )
            onDone()
        }
    }

    private fun finish(words: List<PlacedWord>, state: WordSearchUiState.Playing) {
        stepTimer.stop()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val effectiveDifficulty = state.level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val result = WordSearchScorer.tally(words, effectiveDifficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engageRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.WORD_SEARCH, effectiveDifficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.WORD_SEARCH,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.WORD_SEARCH,
                title = "Tafuta Maneno",
                score = result.foundWords,
                maxScore = result.totalWords,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            var leveledUp = false
            if (state.level != null) {
                val before = gameProgressRepo.getProgress(gameType)
                leveledUp = state.level >= before.highestUnlockedLevel
                gameProgressRepo.completeLevel(gameType, state.level, state.livePoints)
            } else {
                gameProgressRepo.clearSession(gameType)
            }

            _uiState.value = WordSearchUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                words = words,
                unlockedAchievements = unlocked,
                level = state.level,
                pointsEarned = state.livePoints,
                leveledUp = leveledUp,
            )
        }
    }

    fun backToLevelSelect() {
        viewModelScope.launch { showLevelSelect() }
    }

    override fun onCleared() {
        stepTimer.stop()
        super.onCleared()
    }
}
