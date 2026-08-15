package com.swahilib.feature.word.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.entities.content.WordEntity
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

    private val _synonyms = MutableStateFlow<List<WordEntity>>(emptyList())
    val synonyms: StateFlow<List<WordEntity>> get() = _synonyms

    private val _english = MutableStateFlow<String?>(null)
    val english: StateFlow<String?> get() = _english

    private var _currentWord: WordEntity? = null

    fun loadWord(word: WordEntity) {
        _uiState.value = ViewerState.Loading
        _currentWord = word
        _isLiked.value = word.liked
        _title.value = word.title.orEmpty()
        _conjugation.value = word.conjugation.orEmpty().replace("null", "")
        _meanings.value = cleanMeaning(word.meaning).split("|")
        _english.value = word.english?.takeIf { it.isNotBlank() }

        // De-duplicate synonym titles (case-insensitive), then exclude the word itself
        val synonymTitles = word.synonyms
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.map { it.lowercase() }
            ?.distinct()                              // remove duplicate titles
            ?.filter { it != word.title?.lowercase() } // exclude the word itself
            ?.map { s -> word.synonyms!!              // restore original casing from first occurrence
                .split(",")
                .map { it.trim() }
                .first { it.lowercase() == s }
            }
            ?: emptyList()

        if (synonymTitles.isNotEmpty()) {
            viewModelScope.launch {
                wordRepo.getWordsByTitles(synonymTitles).collect { words ->
                    _synonyms.value = words
                        .distinctBy { it.rid }
                        .sortedBy { it.title?.lowercase() }
                }
            }
        } else {
            _synonyms.value = emptyList()
        }

        _uiState.value = ViewerState.Loaded
    }

    fun likeWord(word: WordEntity) {
        viewModelScope.launch {
            val updatedWord = word.copy(liked = !word.liked)
            wordRepo.updateWord(updatedWord)
            _isLiked.value = updatedWord.liked
        }
    }
}