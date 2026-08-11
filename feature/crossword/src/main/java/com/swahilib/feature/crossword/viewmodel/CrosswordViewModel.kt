package com.swahilib.feature.crossword.viewmodel

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
import com.swahilib.core.games.engine.CrosswordScorer
import com.swahilib.core.games.engine.GameLevelConfig
import com.swahilib.core.games.engine.GameStepTimer
import com.swahilib.core.games.generator.CrosswordGenerator
import com.swahilib.core.games.model.CrosswordPuzzle
import com.swahilib.core.games.model.CrosswordResult
import com.swahilib.core.ui.components.game.GameLevelUiModel
import com.swahilib.core.ui.components.game.GameSound
import com.swahilib.core.ui.components.game.GameSoundPlayer
import com.swahilib.feature.crossword.utils.CrosswordSnapshot
import com.swahilib.feature.crossword.utils.CrosswordUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CrosswordViewModel @Inject constructor(
    private val generator: CrosswordGenerator,
    private val engageRepo: EngagementRepo,
    private val gameProgressRepo: GameProgressRepo,
    private val soundPlayer: GameSoundPlayer,
) : ViewModel() {

    private val gameType = StatisticsEngine.EventType.CROSSWORD.name

    private val _uiState = MutableStateFlow<CrosswordUiState>(CrosswordUiState.Loading)
    val uiState: StateFlow<CrosswordUiState> = _uiState.asStateFlow()

    private var challengeId: String? = null
    private var activityId: String? = null
    private var difficulty: Difficulty = Difficulty.BEGINNER
    private var startedAtMs: Long = 0L
    private var contentSeed: Long = 0L

    private val stepTimer = GameStepTimer(scope = viewModelScope, onTick = ::onTick, onExpire = ::onTimeUp)

    fun start(challengeId: String?, activityId: String?, difficulty: Difficulty = Difficulty.BEGINNER) {
        if (_uiState.value !is CrosswordUiState.Loading) return
        this.challengeId = challengeId
        this.activityId = activityId
        this.difficulty = difficulty

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
        _uiState.value = CrosswordUiState.LevelSelect(levels, progress.totalPoints.toInt())
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
        val targetEntries = level?.let { GameLevelConfig.stepCountForLevel(it) } ?: 7

        val saved = if (resume) gameProgressRepo.loadSession(gameType) else null
        val matchesSaved = saved != null && saved.level == (level ?: 0)

        contentSeed = if (matchesSaved) saved!!.contentSeed else Random.nextLong()
        val puzzle = generator.generate(effectiveDifficulty, targetEntries, contentSeed)
        if (puzzle.entries.size < 2) {
            _uiState.value = CrosswordUiState.Empty
            return
        }

        var answers = emptyMap<String, String>()
        if (matchesSaved) {
            val snapshot = runCatching { Json.decodeFromString<CrosswordSnapshot>(saved!!.snapshotJson) }.getOrNull()
            if (snapshot != null) answers = snapshot.answers
        }

        val totalSeconds = level?.let { GameLevelConfig.timerSecondsForLevel(it) * 2 } ?: 180 // whole puzzle, not per-clue
        _uiState.value = CrosswordUiState.Playing(
            puzzle = puzzle,
            level = level,
            previousPoints = progress.totalPoints.toInt(),
            answers = answers,
            secondsRemaining = totalSeconds,
            secondsTotal = totalSeconds,
            easyMode = level?.let { GameLevelConfig.isEasyLevel(it) } ?: false,
        )
        stepTimer.start(totalSeconds)
    }

    private fun onTick(secondsRemaining: Int) {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        _uiState.value = state.copy(secondsRemaining = secondsRemaining)
        if (secondsRemaining in 1..5) soundPlayer.play(GameSound.TICK, volume = 0.5f)
    }

    private fun onTimeUp() {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        soundPlayer.play(GameSound.TIME_UP)
        finish(state)
    }

    fun focusEntry(entryId: String) {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        soundPlayer.play(GameSound.TAP)
        _uiState.value = state.copy(focusedEntryId = entryId)
        persistSnapshot(state)
    }

    fun updateAnswer(entryId: String, text: String) {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        val updated = state.copy(answers = state.answers + (entryId to text))
        _uiState.value = updated
        persistSnapshot(updated)
    }

    /** Easy-mode letter pool: append a tapped letter to whichever entry is focused. */
    fun tapPoolLetter(letter: Char) {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        val entryId = state.focusedEntryId ?: return
        soundPlayer.play(GameSound.TAP)
        val current = state.answers[entryId].orEmpty()
        updateAnswer(entryId, current + letter)
    }

    fun poolBackspace() {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        val entryId = state.focusedEntryId ?: return
        val current = state.answers[entryId].orEmpty()
        if (current.isEmpty()) return
        updateAnswer(entryId, current.dropLast(1))
    }

    private fun persistSnapshot(state: CrosswordUiState.Playing) {
        viewModelScope.launch {
            gameProgressRepo.saveSession(
                gameType = gameType,
                level = state.level ?: 0,
                contentSeed = contentSeed,
                stepIndex = 0,
                livePoints = 0,
                snapshotJson = Json.encodeToString(CrosswordSnapshot.serializer(), CrosswordSnapshot(state.answers)),
            )
        }
    }

    /** "Maliza" - finishes the puzzle now, whatever's filled in counts. Answers are revealed only here. */
    fun finishNow() {
        val state = _uiState.value as? CrosswordUiState.Playing ?: return
        finish(state)
    }

    fun restart() {
        val level = (_uiState.value as? CrosswordUiState.Playing)?.level
        stepTimer.stop()
        _uiState.value = CrosswordUiState.Loading
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
        val state = _uiState.value as? CrosswordUiState.Playing
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
                livePoints = 0,
                snapshotJson = Json.encodeToString(CrosswordSnapshot.serializer(), CrosswordSnapshot(state.answers)),
            )
            onDone()
        }
    }

    private fun finish(state: CrosswordUiState.Playing) {
        stepTimer.stop()
        val secondsSpent = ((System.currentTimeMillis() - startedAtMs) / 1000).toInt().coerceAtLeast(1)
        val effectiveDifficulty = state.level?.let { GameLevelConfig.difficultyForLevel(it) } ?: difficulty
        val result = CrosswordScorer.tally(state.puzzle.entries, state.answers, effectiveDifficulty, secondsSpent)
        val pointsEarned = state.level?.let { GameLevelConfig.pointsPerCorrect(it) * result.correctEntries } ?: (10 * result.correctEntries)

        viewModelScope.launch {
            val cId = challengeId
            val aId = activityId
            var xpEarnedThisSession = result.xpEarned

            if (cId != null && aId != null) {
                engageRepo.markActivityComplete(cId, aId, secondsSpent)
                xpEarnedThisSession = RewardRules.activityXp(ActivityType.CROSSWORD, effectiveDifficulty)
            } else if (result.xpEarned > 0) {
                engageRepo.awardXp(
                    XpAward(
                        source = if (result.isPerfect) XpSource.PERFECT_QUIZ else XpSource.ACTIVITY_COMPLETE,
                        amount = result.xpEarned,
                        activityType = ActivityType.CROSSWORD,
                        secondsSpent = secondsSpent,
                    )
                )
            }

            val unlocked = engageRepo.recordLearningEvent(
                type = StatisticsEngine.EventType.CROSSWORD,
                title = "CrossWord",
                score = result.correctEntries,
                maxScore = result.totalEntries,
                xpEarned = xpEarnedThisSession,
                secondsSpent = secondsSpent,
            )

            var leveledUp = false
            if (state.level != null) {
                val before = gameProgressRepo.getProgress(gameType)
                leveledUp = state.level >= before.highestUnlockedLevel
                gameProgressRepo.completeLevel(gameType, state.level, pointsEarned)
                soundPlayer.play(GameSound.LEVEL_COMPLETE)
            } else {
                gameProgressRepo.clearSession(gameType)
            }

            _uiState.value = CrosswordUiState.Finished(
                result = result.copy(xpEarned = xpEarnedThisSession),
                puzzle = state.puzzle,
                answers = state.answers,
                unlockedAchievements = unlocked,
                level = state.level,
                pointsEarned = pointsEarned,
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
