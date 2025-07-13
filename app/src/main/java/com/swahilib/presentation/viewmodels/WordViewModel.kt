package com.swahilib.presentation.viewmodels

import androidx.lifecycle.*
import com.swahilib.core.utils.cleanMeaning
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
    private val _uiState: MutableStateFlow<ViewerState> = MutableStateFlow(ViewerState.Loading)
    val uiState: StateFlow<ViewerState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _conjugation = MutableStateFlow("")
    val conjugation: StateFlow<String> get() = _conjugation

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _meanings = MutableStateFlow<List<String>>(emptyList())
    val meanings: StateFlow<List<String>> get() = _meanings

    private val _synonyms = MutableStateFlow<List<Word>>(emptyList())
    val synonyms: StateFlow<List<Word>> get() = _synonyms

    fun loadWord(word: Word) {
        _uiState.value = ViewerState.Loading
        _isLiked.value = word.liked

        _title.value = word.title.toString()
        _conjugation.value = word.conjugation.toString()

        _meanings.value = cleanMeaning(word.meaning).split("|")

        // Fetch synonyms as Word objects
        val synonymTitles = word.synonyms
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        if (synonymTitles.isNotEmpty()) {
            viewModelScope.launch {
                wordRepo.getWordsByTitles(synonymTitles).collect { words ->
                    _synonyms.value = words.sortedBy { it.title?.lowercase() }
                }
            }
        } else {
            _synonyms.value = emptyList()
        }

        _uiState.value = ViewerState.Loaded
    }

    fun likeWord(word: Word) {
        viewModelScope.launch {
            val updatedWord = word.copy(liked = !word.liked)
            wordRepo.updateWord(updatedWord)
            _isLiked.value = updatedWord.liked
        }
    }
}
