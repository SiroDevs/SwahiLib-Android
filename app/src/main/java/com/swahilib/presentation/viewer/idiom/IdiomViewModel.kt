package com.swahilib.presentation.viewer.idiom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.utils.cleanMeaning
import com.swahilib.data.models.Idiom
import com.swahilib.domain.entity.ViewerState
import com.swahilib.domain.repos.IdiomRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdiomViewModel @Inject constructor(
    private val idiomRepo: IdiomRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ViewerState> = MutableStateFlow(ViewerState.Loading)
    val uiState: StateFlow<ViewerState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _meanings = MutableStateFlow<List<String>>(emptyList())
    val meanings: StateFlow<List<String>> get() = _meanings

    fun loadIdiom(idiom: Idiom) {
        _uiState.value = ViewerState.Loading
        _isLiked.value = idiom.liked

        _title.value = idiom.title.toString()

        _meanings.value = cleanMeaning(idiom.meaning).split("|")

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