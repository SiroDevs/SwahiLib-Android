package com.swahilib.feature.home.viewmodel

import com.swahilib.feature.home.view.components.HomeTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Owns pure UI navigation state for Home: the selected bottom-nav tab. */
class TabController {
    private val _selectedTab = MutableStateFlow<HomeTab>(HomeTab.Search)
    val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()
    fun setSelectedTab(tab: HomeTab) { _selectedTab.value = tab }
}
