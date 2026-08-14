package com.swahilib.feature.hangman.viewmodel

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
import com.swahilib.core.games.engine.HangmanScorer
import com.swahilib.core.games.generator.HangmanGenerator
import com.swahilib.core.games.model.HangmanRound
import com.swahilib.core.games.model.HangmanSessionResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
import com.swahilib.feature.hangman.utils.HangmanRoundSnapshot
import com.swahilib.feature.hangman.utils.HangmanSnapshot
import com.swahilib.feature.hangman.utils.HangmanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HangmanViewModel @Inject constructor(
    private val generator: HangmanGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val gameType = StatisticsEngine.EventType.HANGMAN.name

    private val _uiState = MutableStateFlow<HangmanUiState>(HangmanUiState.Loading)
    val uiState: StateFlow<HangmanUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L
    private var wordCount: Int = 5

    private val stepTimer = GameStepTimer(
        scope = viewModelScope,
        onTick = ::onTick,
        onExpire = ::onStepExpired,
    )

    fun start(challengeId: String?, activityId: String?, difficulty: Difficulty = Difficulty.BEGINNER, wordCount: Int = 5) {
        if (_uiState.value !is HangmanUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty
        this.wordCount = wordCount

        viewModelScope.launch {
            if (challengeId != null) {
                // Challenge deep-link: skip overview/level select, behave as a single fixed-difficulty session.
                beginSession(level = null, resume = false, practice = false)
            } else {
                val progress = gameProgressRepo.getProgress(gameType)
                _uiState.value = HangmanUiState.Overview(progress.totalPoints.toInt())
            }
        }
    }

    /** "Anza Kucheza" from the overview screen. */
    fun proceedToLevelSelect() {
        viewModelScope.launch { showLevelSelect() }
    }

    /** "Jaribu Kwanza (Mazoezi)" from the overview screen - a short, zero-stakes trial at level 1. */
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
        _uiState.value = HangmanUiState.LevelSelect(levels, progress.totalPoints.toInt())
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

        val saved = if (resume && !practice) gameProgressRepo.loadSession(gameType) else null
        val matchesSaved = saved != null && saved.level == (level ?: 0)

        contentSeed = if (matchesSaved) saved!!.contentSeed else Random.nextLong()
        var rounds = generator.session(effectiveDifficulty, wordCount, contentSeed)
        if (rounds.isEmpty()) {
            _uiState.value = HangmanUiState.Empty
            return
        }

        var index = 0
        var livePoints = 0
        if (matchesSaved) {
            val snapshot = runCatching { Json.decodeFromString<HangmanSnapshot>(saved!!.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                rounds = rounds.mapIndexed { i, round ->
                    snapshot.roundsSoFar.getOrNull(i)?.let {
                        round.copy(guessedLetters = it.guessedLetters.toSet(), wrongGuesses = it.wrongGuesses)
                    } ?: round
                }
                index = saved!!.stepIndex.coerceIn(0, rounds.lastIndex)
                livePoints = saved.livePoints
            }
        }

        soundPlayer.startMusic()
        val totalSeconds = level?.let { GameLevelConfig.timerSecondsForLevel(it) } ?: 60
        _uiState.value = HangmanUiState.Playing(
            rounds = rounds,
            index = index,
            level = level,
            previousPoints = progress.totalPoints.toInt(),
            livePoints = livePoints,
            secondsRemaining = totalSeconds,
            secondsTotal = totalSeconds,
            practice = practice,
        )
        stepTimer.start(totalSeconds)
    }

    private fun onTick(secondsRemaining: Int) {
        val state = _uiState.value as? HangmanUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onStepExpired() {
        val state = _uiState.value as? HangmanUiState.Playing ?: return
        if (state.round.isOver) return
        soundPlayer.play(GameSound.TIME_UP)
        val forcedLoss = state.round.copy(wrongGuesses = state.round.maxWrongGuesses)
        val newRounds = state.rounds.toMutableList().apply { set(state.index, forcedLoss) }
        _uiState.value = state.copy(rounds = newRounds)
        advanceAfterDelay()
    }

    fun guess(letter: Char) {
        val state = _uiState.value as? HangmanUiState.Playing ?: return
        if (state.round.isOver) return
        soundPlayer.play(GameSound.TAP)
        val updated = HangmanScorer.guess(state.round, letter.uppercaseChar())
        val newRounds = state.rounds.toMutableList().apply { set(state.index, updated) }
        _uiState.value = state.copy(rounds = newRounds)
        if (updated.isOver) {
            soundPlayer.play(GameSound.SUBMIT)
            if (updated.isWon) {
                val level = state.level
                val bonus = level?.let { GameLevelConfig.pointsPerCorrect(it) } ?: 10
                _uiState.value = (_uiState.value as HangmanUiState.Playing).copy(livePoints = state.livePoints + bonus)
            }
            advanceAfterDelay()
        }
    }

    private fun advanceAfterDelay() {
        stepTimer.stop()
        viewModelScope.launch {
            delay(650)
            advanceStep()
        }
    }

    private fun advanceStep() {
        val state = _uiState.value as? HangmanUiState.Playing ?: return
        persistSnapshot(state)
        val nextIndex = state.index + 1
        if (nextIndex >= state.rounds.size) {
            finish(state)
        } else {
            val totalSeconds = state.secondsTotal
            _uiState.value = state.copy(index = nextIndex, secondsRemaining = totalSeconds)
            stepTimer.start(totalSeconds)
        }
    }

    private fun persistSnapshot(state: HangmanUiState.Playing) {
        if (state.practice) return // practice runs never touch saved progress
        val snapshot = HangmanSnapshot(
            roundsSoFar = state.rounds.take(state.index + 1).map {
                HangmanRoundSnapshot(it.guessedLetters.joinToString(""), it.wrongGuesses)
            }
        )
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = state.index,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(HangmanSnapshot.serializer(), snapshot),
            )
        }
    }

    fun restart() {
        val state = _uiState.value
        val level = when (state) {
            is HangmanUiState.Playing -> state.level
            else -> null
        }
        val practice = (state as? HangmanUiState.Playing)?.practice ?: false
        stepTimer.stop()
        _uiState.value = HangmanUiState.Loading
        viewModelScope.launch {
            if (!practice) gameProgressRepo.clearSession(gameType)
            beginSession(level = level, resume = false, practice = practice)
        }
    }

    fun discardAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val practice = (_uiState.value as? HangmanUiState.Playing)?.practice ?: false
        viewModelScope.launch {
            if (!practice) gameProgressRepo.clearSession(gameType)
            onDone()
        }
    }

    fun saveAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val state = _uiState.value as? HangmanUiState.Playing
        if (state == null || state.practice) {
            onDone()
            return
        }
        val snapshot = HangmanSnapshot(
            roundsSoFar = state.rounds.take(state.index + 1).map {
                HangmanRoundSnapshot(it.guessedLetters.joinToString(""), it.wrongGuesses)
            }
        )
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = state.index,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(HangmanSnapshot.serializer(), snapshot),
            )
            onDone()
        }
    }

    private fun finish(state: HangmanUiState.Playing) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val effectiveDifficulty = state.level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val result = HangmanScorer.tally(state.rounds, effectiveDifficulty, secondsSpent)

        if (state.practice) {
            // Practice runs never touch XP, points, saved progress, or level unlocks.
            _uiState.value = HangmanUiState.Finished(
                result = result,
                rounds = state.rounds,
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
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.HANGMAN, effectiveDifficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.HANGMAN,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.HANGMAN,
                title = "Mchezo wa Hangman",
                score = result.wonWords,
                maxScore = result.totalWords,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            var leveledUp = false
            if (state.level != null) {
                val before = gameProgressRepo.getProgress(gameType)
                leveledUp = state.level >= before.highestUnlockedLevel
                gameProgressRepo.completeLevel(gameType, state.level, state.livePoints)
                soundPlayer.play(GameSound.LEVEL_COMPLETE)
            } else {
                gameProgressRepo.clearSession(gameType)
            }

            _uiState.value = HangmanUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                rounds = state.rounds,
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
