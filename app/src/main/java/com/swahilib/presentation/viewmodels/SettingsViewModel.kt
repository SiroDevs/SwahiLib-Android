package com.swahilib.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: PreferencesRepository,
//    private val bookRepo: BookRepository,
//    private val songRepo: SongRepository,
) : ViewModel() {
    fun clearData() {
        viewModelScope.launch {
//            bookRepo.deleteAllBooks()
//            songRepo.deleteAllSongs()
            prefsRepo.isDataLoaded = false
        }
    }
}
