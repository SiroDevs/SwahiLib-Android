package com.swahilib.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.EngagementRepo
import com.swahilib.core.data.repos.GameProgressRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.GameLevelConfig
import com.swahilib.core.games.engine.GameStepTimer
import com.swahilib.core.games.engine.SudokuScorer
import com.swahilib.core.games.generator.SudokuGenerator
import com.swahilib.core.games.model.PlacedWord
import com.swahilib.core.games.model.SudokuTheme
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
import com.swahilib.feature.sudoku.utils.SudokuSnapshot
import com.swahilib.feature.sudoku.utils.SudokuUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class SudokuViewModel @Inject constructor(
    private val generator: SudokuGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val gameType = StatisticsEngine.EventType.SUDOKU.name

    private val _uiState = MutableStateFlow<SudokuUiState>(SudokuUiState.Loading)
    val uiState: StateFlow<SudokuUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var theme: SudokuTheme = SudokuTheme.RANDOM
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L

    private val stepTimer = GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onTimeUp)

    fun start(
        challengeId: String?,
        activityId: String?,
        difficulty: Difficulty = Difficulty.BEGINNER,
        theme: SudokuTheme = SudokuTheme.RANDOM,
    ) {
        if (_uiState.value !is SudokuUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty
        this.theme = theme

        viewModelScope.launch {
            if (challengeId != null) {
                beginSession(level = null, resume = false, practice = false)
            } else {
                val progress = gameProgressRepo.getProgress(gameType)
                _uiState.value = SudokuUiState.Overview(progress.totalPoints.toInt())
            }
        }
    }

    fun proceedToLevelSelect() {
        viewModelScope.launch { showLevelSelect() }
    }

    fun startPractice() {
        viewModelScope.launch { beginSession(level = 1, resume = false, practice = true) }
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
        _uiState.value = SudokuUiState.LevelSelect(levels, progress.totalPoints.toInt())
    }

    fun chooseLevel(level: Int) {
        viewModelScope.launch {
            if (!gameProgressRepo.canPlay(gameType, level)) {
                soundPlayer.play(GameSound.LOCKED)
                return@launch
            }
            beginSession(level = level, resume = true, practice = false)
        }
    }

    private suspend fun beginSession(level: Int?, resume: Boolean, practice: Boolean) {
        startedAtMs = System.currentTimeMillis()
        val progress = gameProgressRepo.getProgress(gameType)
        val effectiveDifficulty = level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val wordCount = level?.let { GameLevelConfig.stepCountForLevel(it) } ?: 8
        val easy = level?.let { GameLevelConfig.isEasyLevel(it) } ?: false

        val saved = if (resume && !practice) gameProgressRepo.loadSession(gameType) else null
        val matchesSaved = saved != null && saved.level == (level ?: 0)

        contentSeed = if (matchesSaved) saved!!.contentSeed else Random.nextLong()
        val puzzle = generator.generate(effectiveDifficulty, theme, wordCount, contentSeed, easyFillerPool = easy)
        if (puzzle.words.size < 2) {
            _uiState.value = SudokuUiState.Empty
            return
        }

        var words = puzzle.words
        var livePoints = 0
        if (matchesSaved) {
            val snapshot = runCatching { Json.decodeFromString<SudokuSnapshot>(saved!!.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                val foundSet = snapshot.foundWords.toSet()
                words = words.map { if (it.word in foundSet) it.copy(found = true) else it }
                livePoints = saved.livePoints
            }
        }

        soundPlayer.startMusic()
        val totalSeconds = level?.let { GameLevelConfig.timerSecondsForLevel(it) * 2 } ?: 180
        _uiState.value = SudokuUiState.Playing(
            puzzle = puzzle,
            words = words,
            level = level,
            previousPoints = progress.totalPoints.toInt(),
            livePoints = livePoints,
            secondsRemaining = totalSeconds,
            secondsTotal = totalSeconds,
            easyMode = easy,
            practice = practice,
        )
        stepTimer.start(totalSeconds)
    }

    private fun onTick(secondsRemaining: Int) {
        val state = _uiState.value as? SudokuUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onTimeUp() {
        val state = _uiState.value as? SudokuUiState.Playing ?: return
        soundPlayer.play(GameSound.TIME_UP)
        finish(state.words, state)
    }

    fun tapCell(row: Int, col: Int) {
        val state = _uiState.value as? SudokuUiState.Playing ?: return
        val start = state.selectionStart
        soundPlayer.play(GameSound.TAP)

        if (start == null) {
            _uiState.value = state.copy(selectionStart = row to col, lastMissed = false)
            return
        }

        val match = SudokuScorer.matchSelection(state.words, start.first, start.second, row, col)
        if (match != null) {
            onWordFound(state, match.word)
        } else {
            _uiState.value = state.copy(selectionStart = null, lastMissed = true)
        }
    }

    fun tapPoolLetter(letter: Char) {
        val state = _uiState.value as? SudokuUiState.Playing ?: return
        soundPlayer.play(GameSound.TAP)
        _uiState.value = state.copy(highlightedLetter = letter)
    }

    private fun onWordFound(state: SudokuUiState.Playing, foundWord: String) {
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

    private fun persistSnapshot(state: SudokuUiState.Playing) {
        if (state.practice) return
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = 0,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(
                    SudokuSnapshot.serializer(),
                    SudokuSnapshot(state.words.filter { it.found }.map { it.word }),
                ),
            )
        }
    }

    fun giveUp() {
        val state = _uiState.value as? SudokuUiState.Playing ?: return
        finish(state.words, state)
    }

    fun restart() {
        val state = _uiState.value as? SudokuUiState.Playing
        val level = state?.level
        val practice = state?.practice ?: false
        stepTimer.stop()
        _uiState.value = SudokuUiState.Loading
        viewModelScope.launch {
            if (!practice) gameProgressRepo.clearSession(gameType)
            beginSession(level = level, resume = false, practice = practice)
        }
    }

    fun discardAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val practice = (_uiState.value as? SudokuUiState.Playing)?.practice ?: false
        viewModelScope.launch {
            if (!practice) gameProgressRepo.clearSession(gameType)
            onDone()
        }
    }

    fun saveAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val state = _uiState.value as? SudokuUiState.Playing
        if (state == null || state.practice) {
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
                    SudokuSnapshot.serializer(),
                    SudokuSnapshot(state.words.filter { it.found }.map { it.word }),
                ),
            )
            onDone()
        }
    }

    private fun finish(words: List<PlacedWord>, state: SudokuUiState.Playing) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val effectiveDifficulty = state.level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val result = SudokuScorer.tally(words, effectiveDifficulty, secondsSpent)

        if (state.practice) {
            _uiState.value = SudokuUiState.Finished(
                result = result,
                words = words,
                level = state.level,
                pointsEarned = 0,
                leveledUp = false,
                practice = true,
            )
            return
        }

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engageRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.SUDOKU, effectiveDifficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.SUDOKU,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.SUDOKU,
                title = "Sudoku",
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

            _uiState.value = SudokuUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                words = words,
                unlockedAchievements = unlocked,
                level = state.level,
                pointsEarned = state.livePoints,
                leveledUp = leveledUp,
                practice = false,
            )
        }
    }

    fun backToLevelSelect() {
        viewModelScope.launch { showLevelSelect() }
    }

    override fun onCleared() {
        stepTimer.stop()
        soundPlayer.stopMusic()
        super.onCleared()
    }
}
