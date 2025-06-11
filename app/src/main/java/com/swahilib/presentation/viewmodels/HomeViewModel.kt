package com.swahilib.presentation.viewmodels

import androidx.lifecycle.*
import com.swahilib.data.models.*
import com.swahilib.domain.entities.UiState
import com.swahilib.domain.repository.*
import com.swahilib.presentation.screens.home.widgets.HomeNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepo: WordRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedBook: MutableStateFlow<Int> = MutableStateFlow<Int>(0)
    val selectedBook: StateFlow<Int> = _selectedBook.asStateFlow()

    private val _selectedSong: MutableStateFlow<Int> = MutableStateFlow<Int>(0)
    val selectedSong: StateFlow<Int> = _selectedSong.asStateFlow()

    private val _selectedTab: MutableStateFlow<HomeNavItem> = MutableStateFlow(HomeNavItem.Search)
    val selectedTab: StateFlow<HomeNavItem> = _selectedTab.asStateFlow()

    private val _books = MutableStateFlow<List<Word>>(emptyList())
    val books: StateFlow<List<Word>> get() = _books

    /*private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> get() = _songs

    private val _filtered = MutableStateFlow<List<Song>>(emptyList())
    val filtered: StateFlow<List<Song>> get() = _filtered

    private val _likes = MutableStateFlow<List<Song>>(emptyList())
    val likes: StateFlow<List<Song>> get() = _likes*/

    fun setSelectedTab(tab: HomeNavItem) {
        _selectedTab.value = tab
    }

    fun fetchData() {
        _uiState.tryEmit(UiState.Loading)

        /*viewModelScope.launch {
            val booksList = bookRepo.getAllBooks()
            val songsList = songRepo.getAllSongs()
            _books.value = booksList
            _songs.value = songsList

            val firstBookId = booksList.firstOrNull()?.bookId
            _filtered.value = if (firstBookId != null) {
                songsList.filter { it.book == firstBookId }
            } else {
                emptyList()
            }

            _likes.value = songsList.filter { it.liked }
            _uiState.tryEmit(UiState.Filtered)
        }*/
    }
}
