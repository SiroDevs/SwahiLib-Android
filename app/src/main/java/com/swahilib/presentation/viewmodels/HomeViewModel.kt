package com.swahilib.presentation.viewmodels

import androidx.lifecycle.*
import com.swahilib.data.models.*
import com.swahilib.domain.entities.UiState
import com.swahilib.domain.repository.*
import com.swahilib.presentation.screens.home.widgets.HomeNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val idiomRepo: IdiomRepository,
    private val proverbRepo: ProverbRepository,
    private val sayingRepo: SayingRepository,
    private val wordRepo: WordRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedTab: MutableStateFlow<HomeNavItem> = MutableStateFlow(HomeNavItem.Search)
    val selectedTab: StateFlow<HomeNavItem> = _selectedTab.asStateFlow()

    private val _idioms = MutableStateFlow<List<Idiom>>(emptyList())
    val idioms: StateFlow<List<Idiom>> get() = _idioms

    private val _filteredIdioms = MutableStateFlow<List<Idiom>>(emptyList())
    val filteredIdioms: StateFlow<List<Idiom>> get() = _filteredIdioms

    private val _filteredProverbs = MutableStateFlow<List<Proverb>>(emptyList())
    val filteredProverbs: StateFlow<List<Proverb>> get() = _filteredProverbs

    private val _proverbs = MutableStateFlow<List<Proverb>>(emptyList())
    val proverbs: StateFlow<List<Proverb>> get() = _proverbs

    private val _sayings = MutableStateFlow<List<Saying>>(emptyList())
    val sayings: StateFlow<List<Saying>> get() = _sayings

    private val _filteredSayings = MutableStateFlow<List<Saying>>(emptyList())
    val filteredSayings: StateFlow<List<Saying>> get() = _filteredSayings

    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> get() = _words

    private val _filteredWords = MutableStateFlow<List<Word>>(emptyList())
    val filteredWords: StateFlow<List<Word>> get() = _filteredWords

    fun setSelectedTab(tab: HomeNavItem) {
        _selectedTab.value = tab
    }

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            _idioms.value = idiomRepo.fetchLocalData()
            _filteredIdioms.value = _idioms.value

            _proverbs.value = proverbRepo.fetchLocalData()
            _filteredProverbs.value = _proverbs.value

            _sayings.value = sayingRepo.fetchLocalData()
            _filteredSayings.value = _sayings.value

            _words.value = wordRepo.fetchLocalData()
            _filteredWords.value = _words.value

            _uiState.tryEmit(UiState.Filtered)
        }
    }
}
