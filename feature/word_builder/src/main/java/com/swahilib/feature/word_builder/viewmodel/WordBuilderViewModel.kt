package com.swahilib.feature.word_builder.viewmodel

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
import com.swahilib.core.games.engine.WordBuilderScorer
import com.swahilib.core.games.generator.WordBuilderGenerator
import com.swahilib.core.games.model.ScrambledWord
import com.swahilib.core.games.model.WordBuilderRoundResult
import com.swahilib.core.games.model.WordBuilderSessionResult
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

sealed interface WordBuilderUiState {
    data object Loading : WordBuilderUiState
    data object Empty : WordBuilderUiState

    data class LevelSelect(val levels: List<GameLevelUiModel>, val previousPoints: Int) : WordBuilderUiState

    data class Playing(
        val word: ScrambledWord,
        val roundIndex: Int,
        val totalRounds: Int,
        val level: Int?,
        val previousPoints: Int,
        val livePoints: Int,
        val pickedIndices: List<Int> = emptyList(),
        val revealedCount: Int = 0,
        val hintsUsed: Int = 0,
        val locked: Boolean = false,
        val secondsRemaining: Int,
        val secondsTotal: Int,
    ) : WordBuilderUiState {
        val assembled: String get() = pickedIndices.joinToString("") { word.scrambledLetters[it].toString() }
    }

    data class Finished(
        val result: WordBuilderSessionResult,
        val rounds: List<Pair<ScrambledWord, WordBuilderRoundResult>>,
        val unlockedAchievements: List<Achievement> = emptyList(),
        val level: Int?,
        val pointsEarned: Int,
        val leveledUp: Boolean,
    ) : WordBuilderUiState
}

@Serializable
private data class WordBuilderSnapshot(val roundsSoFar: List<WordBuilderRoundResult>)

@HiltViewModel
class WordBuilderViewModel @Inject constructor(
    private val generator: WordBuilderGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    private val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val gameType = StatisticsEngine.EventType.WORD_BUILDER.name

    private val _uiState = MutableStateFlow<WordBuilderUiState>(WordBuilderUiState.Loading)
    val uiState: StateFlow<WordBuilderUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L
    private var wordCount: Int = 5
    private var session: List<ScrambledWord> = emptyList()
    private var rounds = mutableListOf<WordBuilderRoundResult>()

    private val stepTimer = GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onStepExpired)

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
        this.wordCount = wordCount

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
        _uiState.value = WordBuilderUiState.LevelSelect(levels, progress.totalPoints.toInt())
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
        val count = level?.let { GameLevelConfig.stepCountForLevel(it) } ?: wordCount

        val saved = if (resume) gameProgressRepo.loadSession(gameType) else null
        val matchesSaved = saved != null && saved.level == (level ?: 0)

        contentSeed = if (matchesSaved) saved!!.contentSeed else Random.nextLong()
        session = generator.session(effectiveDifficulty, count, contentSeed)
        if (session.isEmpty()) {
            _uiState.value = WordBuilderUiState.Empty
            return
        }

        rounds = mutableListOf()
        var index = 0
        var livePoints = 0
        if (matchesSaved) {
            val snapshot = runCatching { Json.decodeFromString<WordBuilderSnapshot>(saved!!.snapshotJson) }.getOrNull()
            if (snapshot != null) {
                rounds = snapshot.roundsSoFar.toMutableList()
                index = saved!!.stepIndex.coerceIn(0, session.lastIndex)
                livePoints = saved.livePoints
            }
        }

        val totalSeconds = level?.let { GameLevelConfig.timerSecondsForLevel(it) } ?: 45
        _uiState.value = WordBuilderUiState.Playing(
            word = session[index],
            roundIndex = index,
            totalRounds = session.size,
            level = level,
            previousPoints = progress.totalPoints.toInt(),
            livePoints = livePoints,
            secondsRemaining = totalSeconds,
            secondsTotal = totalSeconds,
        )
        stepTimer.start(totalSeconds)
    }

    private fun onTick(secondsRemaining: Int) {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onStepExpired() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.locked) return
        soundPlayer.play(GameSound.TIME_UP)
        recordRound(state, gaveUp = true)
    }

    fun pickLetter(index: Int) {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.locked || index in state.pickedIndices) return
        soundPlayer.play(GameSound.TAP)
        _uiState.value = state.copy(pickedIndices = state.pickedIndices + index)
    }

    fun clearPicks() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.locked) return
        _uiState.value = state.copy(pickedIndices = state.pickedIndices.take(state.revealedCount))
    }

    fun useHint() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.locked || state.revealedCount >= state.word.answer.length) return
        val nextChar = state.word.answer[state.revealedCount]
        val tileIndex = state.word.scrambledLetters.indices
            .firstOrNull { it !in state.pickedIndices && state.word.scrambledLetters[it] == nextChar }
            ?: return
        soundPlayer.play(GameSound.TAP)
        _uiState.value = state.copy(
            pickedIndices = state.pickedIndices + tileIndex,
            revealedCount = state.revealedCount + 1,
            hintsUsed = state.hintsUsed + 1,
        )
    }

    fun submit() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        if (state.locked) return
        soundPlayer.play(GameSound.SUBMIT)
        recordRound(state, gaveUp = false)
    }

    private fun recordRound(state: WordBuilderUiState.Playing, gaveUp: Boolean) {
        stepTimer.stop()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(0)
        val result = if (gaveUp) {
            WordBuilderRoundResult(state.word.id, correct = false, hintsUsed = state.hintsUsed, secondsSpent = secondsSpent, gaveUp = true)
        } else {
            WordBuilderScorer.checkAnswer(state.word, state.assembled, state.hintsUsed, secondsSpent)
        }
        rounds.add(result)
        val bonus = if (result.correct) (state.level?.let { GameLevelConfig.pointsPerCorrect(it) } ?: 10) else 0
        val updated = state.copy(locked = true, livePoints = state.livePoints + bonus)
        _uiState.value = updated
        persistSnapshot(updated)
        viewModelScope.launch {
            delay(650)
            advanceStep()
        }
    }

    private fun advanceStep() {
        val state = _uiState.value as? WordBuilderUiState.Playing ?: return
        val nextIndex = state.roundIndex + 1
        if (nextIndex >= session.size) {
            finish(state)
        } else {
            val totalSeconds = state.secondsTotal
            _uiState.value = state.copy(
                word = session[nextIndex],
                roundIndex = nextIndex,
                pickedIndices = emptyList(),
                revealedCount = 0,
                hintsUsed = 0,
                locked = false,
                secondsRemaining = totalSeconds,
            )
            stepTimer.start(totalSeconds)
        }
    }

    private fun persistSnapshot(state: WordBuilderUiState.Playing) {
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = state.roundIndex,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(WordBuilderSnapshot.serializer(), WordBuilderSnapshot(rounds.toList())),
            )
        }
    }

    fun restart() {
        val level = (_uiState.value as? WordBuilderUiState.Playing)?.level
        stepTimer.stop()
        _uiState.value = WordBuilderUiState.Loading
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
        val state = _uiState.value as? WordBuilderUiState.Playing
        if (state == null) {
            onDone()
            return
        }
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = state.roundIndex,
                livePoints = state.livePoints,
                snapshotJson = Json.encodeToString(WordBuilderSnapshot.serializer(), WordBuilderSnapshot(rounds.toList())),
            )
            onDone()
        }
    }

    private fun finish(state: WordBuilderUiState.Playing) {
        stepTimer.stop()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val effectiveDifficulty = state.level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val result = WordBuilderScorer.tally(rounds, effectiveDifficulty, secondsSpent)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engageRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.WORD_BUILDER, effectiveDifficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.WORD_BUILDER,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.WORD_BUILDER,
                title = "Jenga Maneno",
                score = result.correctWords,
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

            _uiState.value = WordBuilderUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                rounds = session.zip(rounds),
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
