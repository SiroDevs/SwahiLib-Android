package com.swahilib.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.swahilib.data.models.*
import com.swahilib.domain.entities.*
import com.swahilib.domain.repositories.*
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

    private val _books = MutableStateFlow<List<Selectable<Word>>>(emptyList())
    val books: StateFlow<List<Selectable<Word>>> get() = _books

    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> get() = _words

    fun fetchWords() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            wordRepo.getWords().catch { exception ->
                Log.d("TAG", "fetching books")
                val errorMessage = when (exception) {
                    is HttpException -> "HTTP Error: ${exception.code()}"
                    else -> "Network error: ${exception.message}"
                }
                Log.d("TAG", errorMessage)
                _uiState.tryEmit(UiState.Error(errorMessage))
            }.collect { respData ->
                val selectableWords = respData.map { Selectable(it) }
                _books.emit(selectableWords)
                _uiState.tryEmit(UiState.Loaded)
            }
        }
    }

    fun saveSelectedWords() {
        val selected = getSelectedWords()
        saveWords(selected)
    }

    private fun saveWords(books: List<Word>) {
        _uiState.tryEmit(UiState.Saving)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                for (book in books) {
                    wordRepo.saveWord(book)
                }
                val selectedWords = books.joinToString(",") { it.bookId.toString() }
                //wordRepo.savePrefs(selectedWords)

                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                Log.e("SaveWords", "Failed to save books", e)
                _uiState.emit(UiState.Error("Failed to save books: ${e.message}"))
            }
        }
    }

    fun getSelectedWords(): List<Word> {
        return _books.value.filter { it.isSelected }.map { it.data }
    }
}
