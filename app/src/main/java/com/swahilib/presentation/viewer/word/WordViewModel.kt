package com.swahilib.presentation.viewer.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.utils.cleanMeaning
import com.swahilib.data.models.Word
import com.swahilib.domain.entity.ViewerState
import com.swahilib.domain.repos.WordRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val wordRepo: WordRepo,
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