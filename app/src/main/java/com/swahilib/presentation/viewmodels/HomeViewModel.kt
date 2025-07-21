package com.swahilib.presentation.viewmodels

import androidx.lifecycle.*
import com.swahilib.data.models.*
import com.swahilib.domain.entity.*
import com.swahilib.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val idiomRepo: IdiomRepository,
    private val proverbRepo: ProverbRepository,
    private val sayingRepo: SayingRepository,
    private val wordRepo: WordRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _allIdioms = MutableStateFlow<List<Idiom>>(emptyList())
    private val _filteredIdioms = MutableStateFlow<List<Idiom>>(emptyList())
    val filteredIdioms: StateFlow<List<Idiom>> get() = _filteredIdioms

    private val _allProverbs = MutableStateFlow<List<Proverb>>(emptyList())
    private val _filteredProverbs = MutableStateFlow<List<Proverb>>(emptyList())
    val filteredProverbs: StateFlow<List<Proverb>> get() = _filteredProverbs

    private val _allSayings = MutableStateFlow<List<Saying>>(emptyList())
    private val _filteredSayings = MutableStateFlow<List<Saying>>(emptyList())
    val filteredSayings: StateFlow<List<Saying>> get() = _filteredSayings

    private val _allWords = MutableStateFlow<List<Word>>(emptyList())
    private val _filteredWords = MutableStateFlow<List<Word>>(emptyList())
    val filteredWords: StateFlow<List<Word>> get() = _filteredWords

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)

        viewModelScope.launch {
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
