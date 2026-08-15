package com.swahilib.feature.sentence_builder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.data.repos.games.EngagementRepo
import com.swahilib.core.data.repos.games.GameProgressRepo
import com.swahilib.core.engagement.engine.RewardRules
import com.swahilib.core.engagement.engine.StatisticsEngine
import com.swahilib.core.engagement.model.ActivityType
import com.swahilib.core.engagement.model.Difficulty
import com.swahilib.core.engagement.model.XpAward
import com.swahilib.core.engagement.model.XpSource
import com.swahilib.core.games.engine.GameLevelConfig
import com.swahilib.core.games.engine.GameStepTimer
import com.swahilib.core.games.engine.SentenceScorer
import com.swahilib.core.games.generator.SentenceGenerator
import com.swahilib.core.games.model.SentenceQuestion
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
import com.swahilib.feature.sentence_builder.utils.SentenceSnapshot
import com.swahilib.feature.sentence_builder.utils.SentenceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SentenceBuilderViewModel @Inject constructor(
    private val generator: SentenceGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val gameType = StatisticsEngine.EventType.SENTENCE_BUILDER.name

    private val _uiState = MutableStateFlow<SentenceUiState>(SentenceUiState.Loading)
    val uiState: StateFlow<SentenceUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L
    private var questionCount: Int = 5
    private var questions: List<SentenceQuestion> = emptyList()
    private val results = mutableListOf<Boolean>()

    private val stepTimer = GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onStepExpired)

    fun start(challengeId: String?, activityId: String?, difficulty: Difficulty = Difficulty.BEGINNER, questionCount: Int = 5) {
        if (_uiState.value !is SentenceUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty
        this.questionCount = questionCount

        viewModelScope.launch {
            if (challengeId != null) {
                beginSession(level = null, resume = false, practice = false)
            } else {
                val progress = gameProgressRepo.getProgress(gameType)
                _uiState.value = SentenceUiState.Overview(progress.totalPoints.toInt())
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
        _uiState.value = SentenceUiState.LevelSelect(levels, progress.totalPoints.toInt())
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
        val count = level?.let { GameLevelConfig.stepCountForLevel(it) } ?: questionCount

        val saved = if (resume && !practice) gameProgressRepo.loadSession(gameType) else null
        val matchesSaved = saved != null && saved.level == (level ?: 0)

        contentSeed = if (matchesSaved) saved!!.contentSeed else Random.nextLong()
        questions = generator.generate(effectiveDifficulty, count, contentSeed)
        if (questions.isEmpty()) {
            _uiState.value = SentenceUiState.Empty
            return
        }

        results.clear()
        var index = 0
        var livePoints = 0
        if (matchesSaved) {
            val snapshot = runCatching { Json.decodeFromString<SentenceSnapshot>(saved!!.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                results.addAll(snapshot.correctness)
                index = saved!!.stepIndex.coerceIn(0, questions.lastIndex)
                livePoints = saved.livePoints
            }
        }

        soundPlayer.startMusic()
        val totalSeconds = level?.let { GameLevelConfig.timerSecondsForLevel(it) } ?: 45
        _uiState.value = SentenceUiState.Playing(
            questions = questions,
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
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onStepExpired() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.locked) return
        soundPlayer.play(GameSound.TIME_UP)
        recordRound(state, correct = false)
    }

    fun pickWord(index: Int) {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.locked || state.paused || index in state.pickedIndices) return
        soundPlayer.play(GameSound.TAP)
        _uiState.value = state.copy(pickedIndices = state.pickedIndices + index)
    }

    fun clear() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.locked || state.paused) return
        _uiState.value = state.copy(pickedIndices = emptyList())
    }

    fun submit() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.locked || state.paused || state.pickedIndices.size != state.question.shuffledWords.size) return
        soundPlayer.play(GameSound.SUBMIT)
        val correct = SentenceScorer.check(state.question, state.picked)
        recordRound(state, correct)
    }

    private fun recordRound(state: SentenceUiState.Playing, correct: Boolean) {
        stepTimer.stop()
        results.add(correct)
        val bonus = if (correct) (state.level?.let { GameLevelConfig.pointsPerCorrect(it) } ?: 10) else 0
        val updated = state.copy(locked = true, livePoints = state.livePoints + bonus)
        _uiState.value = updated
        persistSnapshot(updated)
        // Wait for an explicit "Endelea" tap instead of auto-advancing.
    }

    /** "Endelea" - only enabled once the current round is locked in (submitted or timed out). */
    fun continueToNext() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (!state.locked || state.paused) return
        advanceStep()
    }

    /** Pause/resume toggle from the status bar. */
    fun togglePause() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        if (state.paused) {
            _uiState.value = state.copy(paused = false)
            stepTimer.start(state.secondsRemaining)
        } else {
            stepTimer.stop()
            _uiState.value = state.copy(paused = true)
        }
    }

    private fun advanceStep() {
        val state = _uiState.value as? SentenceUiState.Playing ?: return
        val nextIndex = state.index + 1
        if (nextIndex >= questions.size) {
            finish(state)
        } else {
            val totalSeconds = state.secondsTotal
            _uiState.value = state.copy(index = nextIndex, pickedIndices = emptyList(), locked = false, secondsRemaining = totalSeconds)
            stepTimer.start(totalSeconds)
        }
    }

    private fun persistSnapshot(state: SentenceUiState.Playing) {
        if (state.practice) return
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = state.index,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(SentenceSnapshot.serializer(), SentenceSnapshot(results.toList())),
            )
        }
    }

    fun restart() {
        val state = _uiState.value as? SentenceUiState.Playing
        val level = state?.level
        val practice = state?.practice ?: false
        stepTimer.stop()
        _uiState.value = SentenceUiState.Loading
        viewModelScope.launch {
            if (!practice) gameProgressRepo.clearSession(gameType)
            beginSession(level = level, resume = false, practice = practice)
        }
    }

    fun discardAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val practice = (_uiState.value as? SentenceUiState.Playing)?.practice ?: false
        viewModelScope.launch {
            if (!practice) gameProgressRepo.clearSession(gameType)
            onDone()
        }
    }

    fun saveAndExit(onDone: () -> Unit) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val state = _uiState.value as? SentenceUiState.Playing
        if (state == null || state.practice) {
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
                snapshotJson = Json.encodeToString(SentenceSnapshot.serializer(), SentenceSnapshot(results.toList())),
            )
            onDone()
        }
    }

    private fun finish(state: SentenceUiState.Playing) {
        stepTimer.stop()
        soundPlayer.stopMusic()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val effectiveDifficulty = state.level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val result = SentenceScorer.tally(results, effectiveDifficulty, secondsSpent)

        if (state.practice) {
            _uiState.value = SentenceUiState.Finished(
                result = result,
                questions = questions,
                correctness = results.toList(),
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
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.SENTENCE_BUILDER, effectiveDifficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.SENTENCE_BUILDER,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.SENTENCE_BUILDER,
                title = "Panga Sentensi",
                score = result.correctAnswers,
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

            _uiState.value = SentenceUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                questions = questions,
                correctness = results.toList(),
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
