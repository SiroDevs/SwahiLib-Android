package com.swahilib.presentation.viewmodels

import androidx.lifecycle.*
import com.swahilib.data.models.*
import com.swahilib.domain.entities.*
import com.swahilib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordRepo: WordRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    fun loadWord(word: Word) {
        _uiState.value = UiState.Loading
        _isLiked.value = word.liked

        _uiState.value = UiState.Loaded
    }

    fun likeWord(word: Word) {
        viewModelScope.launch {
            /*val updatedWord = word.copy(liked = !word.liked)
            wordRepo.updateWord(updatedWord)
            _isLiked.value = updatedWord.liked*/
        }
    }

}
