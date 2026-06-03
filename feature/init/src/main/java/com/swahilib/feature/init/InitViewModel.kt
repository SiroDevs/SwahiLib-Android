package com.swahilib.feature.init

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.swahilib.core.common.helpers.NetworkUtils
import com.swahilib.core.common.entity.UiState
import com.swahilib.core.data.repos.*
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
                    val idiomInit = async { idiomRepo.fetchRemoteData() }
                    val proverbInit = async { proverbRepo.fetchRemoteData() }
                    val sayingInit = async { sayingRepo.fetchRemoteData() }
                    val wordInit = async { wordRepo.fetchRemoteData() }

                    idiomInit.await()
                    proverbInit.await()
                    sayingInit.await()
                    wordInit.await()

                    Log.d("TAG", "✅ Data fetched and saved successfully.")
                    prefsRepo.isDataLoaded = true
                    prefsRepo.updateAppOpenTime()
                    _uiState.emit(UiState.Saved)
                } else {
                    _uiState.emit(UiState.Error("Masaalale! Hapa bila muunganisho wa intaneti thabiti wewe jua tu hutoboi."))
                }
            } catch (e: Exception) {
                val message = when (e) {
                    is HttpException -> "HTTP Error: ${e.code()}"
                    else -> "Network error: ${e.message}"
                }
                Log.e("TAG", message, e)
                _uiState.emit(UiState.Error(message))
            }
        }
    }
}
