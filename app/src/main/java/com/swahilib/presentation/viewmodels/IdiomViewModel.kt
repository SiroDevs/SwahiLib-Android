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
class IdiomViewModel @Inject constructor(
    private val idiomRepo: IdiomRepository,
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

    private val _synonyms = MutableStateFlow<List<Idiom>>(emptyList())
    val synonyms: StateFlow<List<Idiom>> get() = _synonyms

    fun loadIdiom(idiom: Idiom) {
        _uiState.value = ViewerState.Loading
        _isLiked.value = idiom.liked

        _title.value = idiom.title.toString()
        _conjugation.value = idiom.conjugation.toString()

        _meanings.value = cleanMeaning(idiom.meaning).split("|")

        // Fetch synonyms as Idiom objects
        val synonymTitles = idiom.synonyms
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        if (synonymTitles.isNotEmpty()) {
            viewModelScope.launch {
                idiomRepo.getIdiomsByTitles(synonymTitles).collect { idioms ->
                    _synonyms.value = idioms.sortedBy { it.title?.lowercase() }
                }
            }
        } else {
            _synonyms.value = emptyList()
        }

        _uiState.value = ViewerState.Loaded
    }

    fun likeIdiom(idiom: Idiom) {
        viewModelScope.launch {
            val updatedIdiom = idiom.copy(liked = !idiom.liked)
            idiomRepo.updateIdiom(updatedIdiom)
            _isLiked.value = updatedIdiom.liked
        }
    }
}
