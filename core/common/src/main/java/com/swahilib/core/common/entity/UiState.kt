package com.swahilib.core.common.entity

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Loaded : UiState()
    object Filtered : UiState()
    class Error(val message: String) : UiState()
}

sealed interface ViewerState {
    object Loading : ViewerState
    object Loaded : ViewerState
    data class Liked(val liked: Boolean) : ViewerState
    data class Error(val message: String) : ViewerState
}