package com.swahilib.presentation.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.swahilib.core.utils.Preferences
import com.swahilib.data.models.*
import com.swahilib.domain.entities.*
import com.swahilib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class InitViewModel @Inject constructor(
    private val idiomRepo: IdiomRepository,
    private val proverbRepo: ProverbRepository,
    private val sayingRepo: SayingRepository,
    private val wordRepo: WordRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _idioms = MutableStateFlow<List<Idiom>>(emptyList())
    private val _proverbs = MutableStateFlow<List<Proverb>>(emptyList())
    private val _sayings = MutableStateFlow<List<Saying>>(emptyList())
    private val _words = MutableStateFlow<List<Word>>(emptyList())

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _status = MutableStateFlow("Saving data ...")
    val status: StateFlow<String> = _status.asStateFlow()

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)
        fetchIdioms()
        fetchSayings()
        fetchProverbs()
        fetchWords()
    }

    fun fetchIdioms() {
        viewModelScope.launch {
            Log.d("TAG", "Fetching idioms")
            idiomRepo.fetchRemoteData().catch { exception ->
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                Log.d("TAG", "Fetched ${respData.size} idioms")
                _idioms.emit(respData)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun fetchProverbs() {
        viewModelScope.launch {
            Log.d("TAG", "Fetching proverbs")
            proverbRepo.fetchRemoteData().catch { exception ->
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                Log.d("TAG", "Fetched ${respData.size} proverbs")
                _proverbs.emit(respData)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun fetchSayings() {
        viewModelScope.launch {
            Log.d("TAG", "Fetching sayings")
            sayingRepo.fetchRemoteData().catch { exception ->
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                Log.d("TAG", "Fetched ${respData.size} sayings")
                _sayings.emit(respData)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun fetchWords() {
        viewModelScope.launch {
            Log.d("TAG", "Fetching words")
            wordRepo.fetchRemoteData().catch { exception ->
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                Log.d("TAG", "Fetched ${respData.size} words")
                _words.emit(respData)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun saveData() {
        _uiState.tryEmit(UiState.Saving)
        saveWords()
    }

    fun saveWords() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "Saving data")
            try {
                val words = _words.value
                val total = words.size

                words.forEachIndexed { index, word ->
                    wordRepo.saveWord(word)
                    val percent = ((index + 1).toFloat() / total * 100).toInt()
                    _progress.emit(percent)
                }

                sharedPreferences.edit(commit = true) {
                    putBoolean(Preferences.DATA_LOADED, true)
                }
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                Log.e("SaveData", "Failed to save words", e)
                _uiState.emit(UiState.Error("Failed to save words: ${e.message}"))
            }
        }
    }
}
