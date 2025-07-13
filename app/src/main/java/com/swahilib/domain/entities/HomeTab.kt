package com.swahilib.domain.entities

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