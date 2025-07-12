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

sealed class HomeTab(var title: String) {
    object Words : HomeTab("maneno")
    object Idioms : HomeTab("nahau")
    object Sayings : HomeTab("misemo")
    object Proverbs : HomeTab("methali")
}

val homeTabs = listOf(
    HomeTab.Words,
    HomeTab.Idioms,
    HomeTab.Sayings,
    HomeTab.Proverbs,
)