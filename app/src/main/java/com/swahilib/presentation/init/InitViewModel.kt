package com.swahilib.presentation.init

import android.util.Log
import androidx.lifecycle.*
import com.swahilib.domain.entity.UiState
import com.swahilib.domain.repos.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class InitViewModel @Inject constructor(
    private val idiomRepo: IdiomRepo,
    private val proverbRepo: ProverbRepo,
    private val sayingRepo: SayingRepo,
    private val wordRepo: WordRepo,
    private val prefsRepo: PrefsRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun fetchData() {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            try {
                // Launch all async operations and collect them
                val idiomDeferred = async { idiomRepo.fetchRemoteData() }
                val proverbDeferred = async { proverbRepo.fetchRemoteData() }
                val sayingDeferred = async { sayingRepo.fetchRemoteData() }
                val wordDeferred = async { wordRepo.fetchRemoteData() }

                idiomDeferred.await()
                proverbDeferred.await()
                sayingDeferred.await()
                wordDeferred.await()

                Log.d("TAG", "✅ Data fetched and saved successfully.")
                prefsRepo.isDataLoaded = true
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                val message = when (e) {
                    is HttpException -> "HTTP Error: ${e.code()}"
                    else -> "Network error: ${e.message}"
                }
                Log.e("TAG", message, e)
                prefsRepo.isDataLoaded = false // Reset on error
                _uiState.emit(UiState.Error(message))
            }
        }
    }
}