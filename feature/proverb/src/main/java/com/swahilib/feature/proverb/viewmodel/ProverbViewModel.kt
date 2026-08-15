package com.swahilib.feature.proverb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.database.entities.content.ProverbEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProverbViewModel @Inject constructor(
    private val proverbRepo: ProverbRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ViewerState> = MutableStateFlow(ViewerState.Loading)
    val uiState: StateFlow<ViewerState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _meanings = MutableStateFlow<List<String>>(emptyList())
    val meanings: StateFlow<List<String>> get() = _meanings

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _synonyms = MutableStateFlow<List<ProverbEntity>>(emptyList())
    val synonyms: StateFlow<List<ProverbEntity>> get() = _synonyms

    private val _explanations = MutableStateFlow<List<String>>(emptyList())
    val explanations: StateFlow<List<String>> get() = _explanations

    fun loadProverb(proverb: ProverbEntity) {
        _uiState.value = ViewerState.Loading
        _isLiked.value = proverb.liked

        _title.value = proverb.title.toString()
        _meanings.value = cleanMeaning(proverb.meaning).split("#")
        _explanations.value = cleanMeaning(proverb.conjugation).split("#")

        val synonymTitles = proverb.synonyms
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.distinct()
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

    fun likeProverb(proverb: ProverbEntity) {
        viewModelScope.launch {
            val updatedProverb = proverb.copy(liked = !proverb.liked)
            proverbRepo.updateProverb(updatedProverb)
            _isLiked.value = updatedProverb.liked
        }
    }
}