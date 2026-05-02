package com.swahilib.presentation.init

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.swahilib.core.helpers.NetworkUtils
import com.swahilib.core.repos.PrefsRepo
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

    fun initialize(context: Context) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            try {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    val idiomInitialization = async { idiomRepo.fetchRemoteData() }
                    val proverbInitialization = async { proverbRepo.fetchRemoteData() }
                    val sayingInitialization = async { sayingRepo.fetchRemoteData() }
                    val wordInitialization = async { wordRepo.fetchRemoteData() }

                    idiomInitialization.await()
                    proverbInitialization.await()
                    sayingInitialization.await()
                    wordInitialization.await()

                    Log.d("TAG", "✅ Data fetched and saved successfully.")
                    prefsRepo.isDataLoaded = true
                    _uiState.emit(UiState.Saved)
                } else {
                    _uiState.emit(UiState.Error("Loo! Hapa bila muunganisho wa intaneti unaoaminika hutoboi."))
                }
            } catch (e: Exception) {
                val message = when (e) {
                    is HttpException -> "HTTP Error: ${e.code()}"
                    else -> "Network error: ${e.message}"
                }
                Log.e("TAG", message, e)
                _uiState.emit(UiState.Error(message))
            } finally {
                _uiState.emit(UiState.Error("Loo! Hapa bila muunganisho wa intaneti unaoaminika hutoboi!"))
            }
        }
    }
}