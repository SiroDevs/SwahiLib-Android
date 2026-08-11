package com.swahilib.feature.spelling.viewmodel

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
import com.swahilib.core.games.engine.SpellingScorer
import com.swahilib.core.games.generator.SpellingGenerator
import com.swahilib.core.games.model.SpellingQuestion
import com.swahilib.core.games.model.SpellingResult
import com.swahilib.core.games.model.SpellingRoundResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
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

sealed interface SpellingUiState {
    data object Loading : SpellingUiState
    data object Empty : SpellingUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : SpellingUiState

    data class Playing(
        val questions: List<SpellingQuestion>,
        val index: Int,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val revealedLetters: Int = 0,
        val locked: Boolean = false,
        val secondsRemaining: Int,
        val secondsTotal: Int,
    ) : SpellingUiState {
        val question: SpellingQuestion get() = questions[index]
        val hintText: String get() = question.answer.take(revealedLetters) +
            "_".repeat((question.answer.length - revealedLetters).coerceAtLeast(0))
    }

    data class Finished(
        val result: SpellingResult,
        val questions: List<SpellingQuestion>,
        val rounds: List<SpellingRoundResult>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
    ) : SpellingUiState
}

@Serializable
private data class SpellingSnapshot(val roundsSoFar: List<SpellingRoundResult>)

@HiltViewModel
class SpellingViewModel @Inject constructor(
    private val generator: SpellingGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    private val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val gameType = StatisticsEngine.EventType.SPELLING.name

    private val _uiState = MutableStateFlow<SpellingUiState>(SpellingUiState.Loading)
    val uiState: StateFlow<SpellingUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L
    private var questionCount: Int = 5
    private var questions: List<SpellingQuestion> = emptyList()
    private val rounds = mutableListOf<SpellingRoundResult>()

    private val stepTimer = GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onStepExpired)

    fun start(challengeId: String?, activityId: String?, difficulty: Difficulty = Difficulty.BEGINNER, questionCount: Int = 5) {
        if (_uiState.value !is SpellingUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty
        this.questionCount = questionCount

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
        _uiState.value = SpellingUiState.LevelSelect(levels, progress.totalPoints.toInt())
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
        val count = level?.let { GameLevelConfig.stepCountForLevel(it) } ?: questionCount

        val saved = if (resume) gameProgressRepo.loadSession(gameType) else null
        val matchesSaved = saved != null && saved.level == (level ?: 0)

        contentSeed = if (matchesSaved) saved!!.contentSeed else Random.nextLong()
        questions = generator.generate(effectiveDifficulty, count, contentSeed)
        if (questions.isEmpty()) {
            _uiState.value = SpellingUiState.Empty
            return
        }

        rounds.clear()
        var index = 0
        var livePoints = 0
        if (matchesSaved) {
            val snapshot = runCatching { Json.decodeFromString<SpellingSnapshot>(saved!!.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                rounds.addAll(snapshot.roundsSoFar)
                index = saved!!.stepIndex.coerceIn(0, questions.lastIndex)
                livePoints = saved.livePoints
            }
        }

        val totalSeconds = level?.let { GameLevelConfig.timerSecondsForLevel(it) } ?: 45
        _uiState.value = SpellingUiState.Playing(
            questions = questions,
            index = index,
            level = level,
            previousPoints = progress.totalPoints.toInt(),
            livePoints = livePoints,
            secondsRemaining = totalSeconds,
            secondsTotal = totalSeconds,
        )
        stepTimer.start(totalSeconds)
    }

    private fun onTick(secondsRemaining: Int) {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onStepExpired() {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        if (state.locked) return
        soundPlayer.play(GameSound.TIME_UP)
        recordRound(state, typed = "")
    }

    fun useHint() {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        if (state.locked || state.revealedLetters >= state.question.answer.length - 1) return
        soundPlayer.play(GameSound.TAP)
        _uiState.value = state.copy(revealedLetters = state.revealedLetters + 1)
    }

    fun submit(typed: String) {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        if (state.locked) return
        soundPlayer.play(GameSound.SUBMIT)
        recordRound(state, typed)
    }

    private fun recordRound(state: SpellingUiState.Playing, typed: String) {
        stepTimer.stop()
        val result = SpellingScorer.checkAnswer(state.question, typed, state.revealedLetters)
        rounds.add(result)
        val bonus = if (result.fullyCorrect) (state.level?.let { GameLevelConfig.pointsPerCorrect(it) } ?: 10) else 0
        val updated = state.copy(locked = true, livePoints = state.livePoints + bonus)
        _uiState.value = updated
        persistSnapshot(updated)
        viewModelScope.launch {
            delay(650)
            advanceStep()
        }
    }

    private fun advanceStep() {
        val state = _uiState.value as? SpellingUiState.Playing ?: return
        val nextIndex = state.index + 1
        if (nextIndex >= questions.size) {
            finish(state)
        } else {
            val totalSeconds = state.secondsTotal
            _uiState.value = state.copy(index = nextIndex, revealedLetters = 0, locked = false, secondsRemaining = totalSeconds)
            stepTimer.start(totalSeconds)
        }
    }

    private fun persistSnapshot(state: SpellingUiState.Playing) {
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = state.index,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(SpellingSnapshot.serializer(), SpellingSnapshot(rounds.toList())),
            )
        }
    }

    fun restart() {
        val level = (_uiState.value as? SpellingUiState.Playing)?.level
        stepTimer.stop()
        _uiState.value = SpellingUiState.Loading
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
        val state = _uiState.value as? SpellingUiState.Playing
        if (state == null) {
            onDone()
            return
        }
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = state.index,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(SpellingSnapshot.serializer(), SpellingSnapshot(rounds.toList())),
            )
            onDone()
        }
    }

    private fun finish(state: SpellingUiState.Playing) {
        stepTimer.stop()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val effectiveDifficulty = state.level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val result = SpellingScorer.tally(rounds, effectiveDifficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engageRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.SPELLING_CHALLENGE, effectiveDifficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.SPELLING_CHALLENGE,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.SPELLING,
                title = "Tahajia (Spellcheck)",
                score = result.fullyCorrectCount,
                maxScore = result.totalQuestions,
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

            _uiState.value = SpellingUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                questions = questions,
                rounds = rounds.toList(),
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
