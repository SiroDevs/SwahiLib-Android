package com.swahilib.feature.home.viewmodel

import com.swahilib.feature.home.view.components.HomeTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Owns pure UI navigation state for Home: the selected bottom-nav tab and history sub-tab. */
class TabController {
    private val _selectedTab = MutableStateFlow<HomeTab>(HomeTab.Search)
    val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()
    fun setSelectedTab(tab: HomeTab) { _selectedTab.value = tab }

    private val _historySubTab = MutableStateFlow(HomeViewModel.HistorySubTab.USOMAJI)
    val historySubTab: StateFlow<HomeViewModel.HistorySubTab> = _historySubTab.asStateFlow()
    fun setHistorySubTab(tab: HomeViewModel.HistorySubTab) { _historySubTab.value = tab }
}
