package com.swahilib.feature.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.library.LibraryCatalog
import com.swahilib.core.common.library.LibraryCollectionConfig
import com.swahilib.core.common.entity.LibraryDisplayItem
import com.swahilib.core.data.repos.content.LibraryRepo
import com.swahilib.core.network.api.KamusiApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepo: LibraryRepo,
) : ViewModel() {
    val collections: List<LibraryCollectionConfig> = LibraryCatalog.ALL

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    fun configFor(key: String): LibraryCollectionConfig? = LibraryCatalog.byKey(key)

    fun itemsFor(key: String): StateFlow<List<LibraryDisplayItem>> =
        libraryRepo.displayItemsFor(key)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun ensureLoaded(key: String) {
        viewModelScope.launch {
            if (libraryRepo.hasLocalData(key)) return@launch
            val endpoint = KamusiApi.Endpoint.forLibraryKey(key) ?: return@launch
            _isSyncing.value = true
            libraryRepo.fetchRemoteData(endpoint)
            _isSyncing.value = false
        }
    }

    fun refresh(key: String) {
        viewModelScope.launch {
            val endpoint = KamusiApi.Endpoint.forLibraryKey(key) ?: return@launch
            _isSyncing.value = true
            libraryRepo.fetchRemoteData(endpoint)
            _isSyncing.value = false
        }
    }
}
