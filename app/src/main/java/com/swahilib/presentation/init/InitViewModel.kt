package com.swahilib.presentation.init

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.data.models.Idiom
import com.swahilib.data.models.Proverb
import com.swahilib.data.models.Saying
import com.swahilib.data.models.Word
import com.swahilib.domain.entity.UiState
import com.swahilib.domain.repos.IdiomRepo
import com.swahilib.domain.repos.PrefsRepo
import com.swahilib.domain.repos.ProverbRepo
import com.swahilib.domain.repos.SayingRepo
import com.swahilib.domain.repos.WordRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _idioms = MutableStateFlow<List<Idiom>>(emptyList())
    private val _proverbs = MutableStateFlow<List<Proverb>>(emptyList())
    private val _sayings = MutableStateFlow<List<Saying>>(emptyList())
    private val _words = MutableStateFlow<List<Word>>(emptyList())

    fun fetchData() {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            try {
                val idioms = async {
                    idiomRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                val proverbs = async {
                    proverbRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                val sayings = async {
                    sayingRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                val words = async {
                    wordRepo.fetchRemoteData()
                        .firstOrNull() ?: emptyList()
                }

                _idioms.emit(idioms.await())
                _proverbs.emit(proverbs.await())
                _sayings.emit(sayings.await())
                _words.emit(words.await())

                saveData()
                _uiState.emit(UiState.Loaded)
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

    fun saveData() {
        viewModelScope.launch {
            _uiState.emit(UiState.Saving)
            try {
                async { idiomRepo.saveIdioms(_idioms.value) }
                async { sayingRepo.saveSayings(_sayings.value) }
                async { proverbRepo.saveProverbs(_proverbs.value) }
                async { wordRepo.saveWords(_words.value) }.await()

                prefsRepo.isDataLoaded = true
                _uiState.emit(UiState.Saved)
            } catch (e: Exception) {
                prefsRepo.isDataLoaded = false
                Log.e("SaveSongs", "Failed to save data", e)
                _uiState.emit(UiState.Error("Failed to save data: ${e.message}"))
            }
        }
    }
}