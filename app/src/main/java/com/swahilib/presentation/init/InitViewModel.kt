package com.swahilib.presentation.init

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.data.models.Idiom
import com.swahilib.data.models.Proverb
import com.swahilib.data.models.Saying
import com.swahilib.data.models.Word
import com.swahilib.domain.entity.UiState
import com.swahilib.domain.repository.IdiomRepository
import com.swahilib.domain.repository.PreferencesRepository
import com.swahilib.domain.repository.ProverbRepository
import com.swahilib.domain.repository.SayingRepository
import com.swahilib.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class InitViewModel @Inject constructor(
    private val idiomRepo: IdiomRepository,
    private val proverbRepo: ProverbRepository,
    private val sayingRepo: SayingRepository,
    private val wordRepo: WordRepository,
    private val prefsRepo: PreferencesRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _idioms = MutableStateFlow<List<Idiom>>(emptyList())
    private val _proverbs = MutableStateFlow<List<Proverb>>(emptyList())
    private val _sayings = MutableStateFlow<List<Saying>>(emptyList())
    private val _words = MutableStateFlow<List<Word>>(emptyList())

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _status = MutableStateFlow("Inapakia data ...")
    val status: StateFlow<String> = _status.asStateFlow()

    fun fetchData() {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            try {
                val idioms = async {
                    idiomRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                val proverbs = async {
                    proverbRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                val sayings = async {
                    sayingRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                val words = async {
                    wordRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                _idioms.emit(idioms.await())
                _proverbs.emit(proverbs.await())
                _sayings.emit(sayings.await())
                _words.emit(words.await())

                _uiState.emit(UiState.Loaded)
            } catch (e: Exception) {
                val message = when (e) {
                    is HttpException -> "HTTP Error: ${e.code()}"
                    else -> "Network error: ${e.message}"
                }
                Log.e("TAG", message, e)
                _uiState.emit(UiState.Error(message))
            }
        }
    }

    fun saveData() {
        viewModelScope.launch {
            _uiState.emit(UiState.Saving)
            try {
                val idiomsJob = async { saveIdioms() }
                val proverbsJob = async { saveProverbs() }
                val sayingsJob = async { saveSayings() }
                val wordsJob = async { saveWords() }

                idiomsJob.await()
                proverbsJob.await()
                sayingsJob.await()
                wordsJob.await()

                prefsRepo.isDataLoaded = true
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                prefsRepo.isDataLoaded = false
                Log.e("SaveSongs", "Failed to save data", e)
                _uiState.emit(UiState.Error("Failed to save data: ${e.message}"))
            }
        }
    }

    suspend fun saveIdioms() = withContext(Dispatchers.IO) {
        Log.d("TAG", "Saving idioms")
        _status.emit("Inahifadhi nahau 527 ...")
        val idioms = _idioms.value

        idioms.forEachIndexed { index, idiom ->
            idiomRepo.saveIdiom(idiom)
            val percent = ((index + 1).toFloat() / idioms.size * 100).toInt()
            _progress.emit(percent)
        }
    }

    suspend fun saveProverbs() = withContext(Dispatchers.IO) {
        Log.d("TAG", "Saving proverbs")
        val proverbs = _proverbs.value
        val total = proverbs.size
        _status.emit("Inahifadhi methali $total ...")

        proverbs.forEachIndexed { index, proverb ->
            proverbRepo.saveProverb(proverb)
            val percent = ((index + 1).toFloat() / total * 100).toInt()
            _progress.emit(percent)
        }
    }

    suspend fun saveSayings() = withContext(Dispatchers.IO) {
        Log.d("TAG", "Saving sayings")
        val sayings = _sayings.value
        val total = sayings.size
        _status.emit("Inahifadhi misemo $total ...")

        sayings.forEachIndexed { index, saying ->
            sayingRepo.saveSaying(saying)
            val percent = ((index + 1).toFloat() / total * 100).toInt()
            _progress.emit(percent)
        }
    }

    suspend fun saveWords() = withContext(Dispatchers.IO) {
        Log.d("TAG", "Saving words")
        val words = _words.value
        val total = words.size
        _status.emit("Inahifadhi maneno $total ...")

        words.forEachIndexed { index, word ->
            wordRepo.saveWord(word)
            val percent = ((index + 1).toFloat() / total * 100).toInt()
            _progress.emit(percent)
        }
    }
}