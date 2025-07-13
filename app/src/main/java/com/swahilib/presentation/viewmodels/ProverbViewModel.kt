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
class ProverbViewModel @Inject constructor(
    private val proverbRepo: ProverbRepository,
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

    private val _synonyms = MutableStateFlow<List<Proverb>>(emptyList())
    val synonyms: StateFlow<List<Proverb>> get() = _synonyms

    fun loadProverb(proverb: Proverb) {
        _uiState.value = ViewerState.Loading
        _isLiked.value = proverb.liked

        _title.value = proverb.title.toString()
        _conjugation.value = proverb.conjugation.toString()

        _meanings.value = cleanMeaning(proverb.meaning).split("|")

        // Fetch synonyms as Proverb objects
        val synonymTitles = proverb.synonyms
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        if (synonymTitles.isNotEmpty()) {
            viewModelScope.launch {
                proverbRepo.getProverbsByTitles(synonymTitles).collect { proverbs ->
                    _synonyms.value = proverbs.sortedBy { it.title?.lowercase() }
                }
            }
        } else {
            _synonyms.value = emptyList()
        }

        _uiState.value = ViewerState.Loaded
    }

    fun likeProverb(proverb: Proverb) {
        viewModelScope.launch {
            val updatedProverb = proverb.copy(liked = !proverb.liked)
            proverbRepo.updateProverb(updatedProverb)
            _isLiked.value = updatedProverb.liked
        }
    }
}
