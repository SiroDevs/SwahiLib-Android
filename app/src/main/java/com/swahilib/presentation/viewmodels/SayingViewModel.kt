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
class SayingViewModel @Inject constructor(
    private val sayingRepo: SayingRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ViewerState> = MutableStateFlow(ViewerState.Loading)
    val uiState: StateFlow<ViewerState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _meanings = MutableStateFlow<List<String>>(emptyList())
    val meanings: StateFlow<List<String>> get() = _meanings

    fun loadSaying(saying: Saying) {
        _uiState.value = ViewerState.Loading
        _isLiked.value = saying.liked

        _title.value = saying.title.toString()

        _meanings.value = cleanMeaning(saying.meaning).split("|")

        _uiState.value = ViewerState.Loaded
    }

    fun likeSaying(saying: Saying) {
        viewModelScope.launch {
            val updatedSaying = saying.copy(liked = !saying.liked)
            sayingRepo.updateSaying(updatedSaying)
            _isLiked.value = updatedSaying.liked
        }
    }
}
