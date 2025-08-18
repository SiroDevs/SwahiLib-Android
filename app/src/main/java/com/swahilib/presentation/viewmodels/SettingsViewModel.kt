package com.swahilib.presentation.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.domain.repository.PrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PrefsRepository,
//    private val bookRepo: BookRepository,
//    private val songRepo: SongRepository,
) : ViewModel() {

    var horizontalSlides by mutableStateOf(prefsRepo.horizontalSlides)
        private set

    fun updateHorizontalSlides(enabled: Boolean) {
        horizontalSlides = enabled
        prefsRepo.horizontalSlides = enabled
    }

    fun updateSelection(enabled: Boolean) {
        prefsRepo.initialBooks = prefsRepo.selectedBooks
        prefsRepo.selectAfresh = enabled
    }

    fun clearData() {
        viewModelScope.launch {
//            bookRepo.deleteAllBooks()
//            songRepo.deleteAllSongs()
            prefsRepo.isDataLoaded = false
            prefsRepo.isDataSelected = false
            prefsRepo.selectedBooks = ""
        }
    }
}
