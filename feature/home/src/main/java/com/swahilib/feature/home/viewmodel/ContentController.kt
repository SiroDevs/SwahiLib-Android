package com.swahilib.feature.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.data.worker.SyncWorker
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Owns the four content types (words/idioms/proverbs/sayings): loading them
 * from the DB (falling back to observing the install sync worker on a cold
 * start), search/filter matching, and likes. These stay together rather than
 * being split further because likes and filtering both mutate the same
 * underlying "all X" / "filtered X" lists.
 */
class ContentController(
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
    private val prefsRepo: PrefsRepo,
    private val context: Context,
    private val scope: CoroutineScope,
) {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _allIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    private val _allProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    private val _allSayings = MutableStateFlow<List<SayingEntity>>(emptyList())
    private val _allWords = MutableStateFlow<List<WordEntity>>(emptyList())

    private val _filteredIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    private val _filteredProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    private val _filteredSayings = MutableStateFlow<List<SayingEntity>>(emptyList())
    private val _filteredWords = MutableStateFlow<List<WordEntity>>(emptyList())
    val filteredIdioms: StateFlow<List<IdiomEntity>> get() = _filteredIdioms
    val filteredProverbs: StateFlow<List<ProverbEntity>> get() = _filteredProverbs
    val filteredSayings: StateFlow<List<SayingEntity>> get() = _filteredSayings
    val filteredWords: StateFlow<List<WordEntity>> get() = _filteredWords

    private var dataFetched = false

    fun fetchData(force: Boolean = false) {
        if (dataFetched && !force) return
        dataFetched = true

        scope.launch {
            _uiState.tryEmit(UiState.Loading)
            loadFromDb()

            if (_allWords.value.isEmpty() && _allIdioms.value.isEmpty() &&
                _allProverbs.value.isEmpty() && _allSayings.value.isEmpty()) {
                observeInstallSyncWorker()
            }
        }
    }

    private suspend fun loadFromDb() {
        _uiState.tryEmit(UiState.Loading)
        _allIdioms.value = idiomRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
        _filteredIdioms.value = _allIdioms.value

        _allProverbs.value = proverbRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
        _filteredProverbs.value = _allProverbs.value

        _allSayings.value = sayingRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
        _filteredSayings.value = _allSayings.value

        _allWords.value = wordRepo.fetchLocalData().sortedBy { it.title?.lowercase() }
        _filteredWords.value = _allWords.value

        if (prefsRepo.isDataLoaded && _allWords.value.isNotEmpty()) {
            _uiState.tryEmit(UiState.Filtered)
        } else if (!prefsRepo.isDataLoaded) {
            _uiState.tryEmit(UiState.Loading)
        } else {
            _uiState.tryEmit(UiState.Filtered)
        }
    }

    private fun observeInstallSyncWorker() {
        scope.launch(Dispatchers.Main) {
            _uiState.tryEmit(UiState.Loading)
            try {
                WorkManager.getInstance(context)
                    .getWorkInfosByTagFlow(SyncWorker.TAG)
                    .collect { workInfoList ->
                        val info = workInfoList.firstOrNull() ?: return@collect
                        when (info.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                loadFromDb()
                                return@collect
                            }
                            WorkInfo.State.FAILED,
                            WorkInfo.State.CANCELLED -> {
                                if (_allWords.value.isEmpty() && _allIdioms.value.isEmpty() &&
                                    _allProverbs.value.isEmpty() && _allSayings.value.isEmpty()) {
                                    _uiState.tryEmit(UiState.Filtered)
                                } else {
                                    _uiState.tryEmit(UiState.Error("Failed to load data"))
                                }
                                return@collect
                            }
                            WorkInfo.State.RUNNING,
                            WorkInfo.State.ENQUEUED -> {
                                _uiState.tryEmit(UiState.Loading)
                            }
                            else -> { /* other states */ }
                        }
                    }
            } catch (e: Exception) {
                Log.e("ContentController", "Worker observation error", e)
                if (_allWords.value.isEmpty() && _allIdioms.value.isEmpty() &&
                    _allProverbs.value.isEmpty() && _allSayings.value.isEmpty()) {
                    _uiState.tryEmit(UiState.Filtered)
                } else {
                    _uiState.tryEmit(UiState.Error("Error loading data"))
                }
            }
        }
    }

    fun filterData(query: String) {
        val q = query.lowercase().trim()
        if (q.isEmpty()) {
            _filteredIdioms.value = _allIdioms.value
            _filteredProverbs.value = _allProverbs.value
            _filteredSayings.value = _allSayings.value
            _filteredWords.value = _allWords.value
            return
        }

        // Match on title first (ranked), falling back to a match anywhere in the
        // meaning (and, for words, the English equivalent) so e.g. searching
        // "farming" surfaces "Chaa" even though the title itself doesn't contain it.
        _filteredIdioms.value = fuzzyRank(_allIdioms.value, q, { it.title }) { listOf(it.meaning) }
        _filteredProverbs.value = fuzzyRank(_allProverbs.value, q, { it.title }) { listOf(it.meaning) }
        _filteredSayings.value = fuzzyRank(_allSayings.value, q, { it.title }) { listOf(it.meaning) }
        _filteredWords.value = fuzzyRank(_allWords.value, q, { it.title }) { listOf(it.meaning, it.english) }
    }

    private fun <T> fuzzyRank(
        items: List<T>,
        query: String,
        title: (T) -> String?,
        extraFields: (T) -> List<String?> = { emptyList() },
    ): List<T> {
        val maxDist = max(1, query.length / 3)
        return items
            .mapNotNull { item ->
                val t = title(item)?.lowercase()
                val titlePriority = when {
                    t == null -> null
                    t == query -> 0
                    t.startsWith(query) -> 1
                    t.contains(query) -> 2
                    editDistance(t, query) <= maxDist -> 3
                    else -> null
                }
                val priority = titlePriority ?: run {
                    val matchesExtra = extraFields(item).any { it?.lowercase()?.contains(query) == true }
                    if (matchesExtra) 4 else return@mapNotNull null
                }
                item to priority
            }
            .sortedWith(compareBy({ it.second }, { title(it.first)?.lowercase() }))
            .map { it.first }
    }

    private fun editDistance(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        // Only compute on first window to keep it O(n) space
        val s = if (a.length <= b.length) a else b
        val t = if (a.length <= b.length) b else a
        var prev = IntArray(s.length + 1) { it }
        for (j in 1..t.length) {
            val curr = IntArray(s.length + 1)
            curr[0] = j
            for (i in 1..s.length) {
                curr[i] = if (s[i - 1] == t[j - 1]) prev[i - 1]
                else 1 + minOf(prev[i], curr[i - 1], prev[i - 1])
            }
            prev = curr
        }
        return prev[s.length]
    }

    fun likeWord(word: WordEntity) {
        scope.launch {
            val updated = word.copy(liked = !word.liked)
            wordRepo.updateWord(updated)
            _allWords.value = _allWords.value.map { if (it.rid == word.rid) updated else it }
            _filteredWords.value =
                _filteredWords.value.map { if (it.rid == word.rid) updated else it }
        }
    }

    fun likeIdiom(idiom: IdiomEntity) {
        scope.launch {
            val updated = idiom.copy(liked = !idiom.liked)
            idiomRepo.updateIdiom(updated)
            _allIdioms.value = _allIdioms.value.map { if (it.rid == idiom.rid) updated else it }
            _filteredIdioms.value =
                _filteredIdioms.value.map { if (it.rid == idiom.rid) updated else it }
        }
    }

    fun likeProverb(proverb: ProverbEntity) {
        scope.launch {
            val updated = proverb.copy(liked = !proverb.liked)
            proverbRepo.updateProverb(updated)
            _allProverbs.value =
                _allProverbs.value.map { if (it.rid == proverb.rid) updated else it }
            _filteredProverbs.value =
                _filteredProverbs.value.map { if (it.rid == proverb.rid) updated else it }
        }
    }

    fun likeSaying(saying: SayingEntity) {
        scope.launch {
            val updated = saying.copy(liked = !saying.liked)
            sayingRepo.updateSaying(updated)
            _allSayings.value = _allSayings.value.map { if (it.rid == saying.rid) updated else it }
            _filteredSayings.value =
                _filteredSayings.value.map { if (it.rid == saying.rid) updated else it }
        }
    }
}
