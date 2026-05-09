package com.swahilib.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.common.entity.HomeTab
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.SayingRepo
import com.swahilib.core.data.repos.WordRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
    private val prefsRepo: PrefsRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _allIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    private val _filteredIdioms = MutableStateFlow<List<IdiomEntity>>(emptyList())
    val filteredIdioms: StateFlow<List<IdiomEntity>> get() = _filteredIdioms

    private val _allProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    private val _filteredProverbs = MutableStateFlow<List<ProverbEntity>>(emptyList())
    val filteredProverbs: StateFlow<List<ProverbEntity>> get() = _filteredProverbs

    private val _allSayings = MutableStateFlow<List<SayingEntity>>(emptyList())
    private val _filteredSayings = MutableStateFlow<List<SayingEntity>>(emptyList())
    val filteredSayings: StateFlow<List<SayingEntity>> get() = _filteredSayings

    private val _allWords = MutableStateFlow<List<WordEntity>>(emptyList())
    private val _filteredWords = MutableStateFlow<List<WordEntity>>(emptyList())
    val filteredWords: StateFlow<List<WordEntity>> get() = _filteredWords

    private val _canShowPaywall = MutableStateFlow(false)
    val canShowPaywall: StateFlow<Boolean> = _canShowPaywall.asStateFlow()

    val lastHomeTab = prefsRepo.lastHomeTab

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
            _canShowPaywall.value = prefsRepo.canShowPaywall
            _allIdioms.value = idiomRepo.fetchLocalData()
            _filteredIdioms.value = _allIdioms.value

            _allProverbs.value = proverbRepo.fetchLocalData()
            _filteredProverbs.value = _allProverbs.value

            _allSayings.value = sayingRepo.fetchLocalData()
            _filteredSayings.value = _allSayings.value

            _allWords.value = wordRepo.fetchLocalData()
            _filteredWords.value = _allWords.value

            _uiState.tryEmit(UiState.Filtered)
        }
    }

    fun filterData(tab: HomeTab, qry: String) {
        _uiState.tryEmit(UiState.Loading)
        val query = qry.lowercase()

        when (tab) {
            HomeTab.Idioms -> {
                _filteredIdioms.value = _allIdioms.value.filter {
                    it.title?.startsWith(query) == true
                }
            }

            HomeTab.Proverbs -> {
                _filteredProverbs.value = _allProverbs.value.filter {
                    it.title?.startsWith(query) == true
                }
            }

            HomeTab.Sayings -> {
                _filteredSayings.value = _allSayings.value.filter {
                    it.title?.startsWith(query) == true
                }
            }

            HomeTab.Words -> {
                _filteredWords.value = _allWords.value.filter {
                    it.title?.startsWith(query) == true
                }
            }
        }

        _uiState.tryEmit(UiState.Filtered)
    }
}