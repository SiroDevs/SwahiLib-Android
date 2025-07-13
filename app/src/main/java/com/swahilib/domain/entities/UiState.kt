package com.swahilib.domain.entities

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Loaded : UiState()
    object Filtered : UiState()
    object Saving : UiState()
    object Saved : UiState()
    class Error(val errorMessage: String) : UiState()
}

sealed interface ViewerState {
    object Loading : ViewerState
    data class Loaded(val meanings: List<String>, val synonyms: List<String>) : ViewerState
    data class Liked(val liked: Boolean) : ViewerState
    data class Error(val message: String) : ViewerState
}