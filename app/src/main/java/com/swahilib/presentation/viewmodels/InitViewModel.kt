package com.swahilib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.swahilib.data.models.*
import com.swahilib.domain.entities.*
import com.swahilib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class InitViewModel @Inject constructor(
    private val wordRepo: WordRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _words = MutableStateFlow<List<Word>>(emptyList())

    fun fetchWords() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            wordRepo.fetchRemoteData().catch { exception ->
                Log.d("TAG", "fetching words")
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                _words.emit(respData)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun saveData() {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _words.value.forEach {
                    wordRepo.saveWord(it)
                }
//                wordRepo.savePrefs()
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                Log.e("SaveData", "Failed to save words", e)
                _uiState.emit(UiState.Error("Failed to save words: ${e.message}"))
            }
        }
    }
}
