/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swahilib.feature.idiom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.common.utils.cleanMeaning
import com.swahilib.core.data.repos.IdiomRepo
import com.swahilib.core.database.model.IdiomEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdiomViewModel @Inject constructor(
    private val idiomRepo: IdiomRepo,
) : ViewModel() {
    private val _uiState: MutableStateFlow<ViewerState> = MutableStateFlow(ViewerState.Loading)
    val uiState: StateFlow<ViewerState> = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> get() = _isLiked

    private val _meanings = MutableStateFlow<List<String>>(emptyList())
    val meanings: StateFlow<List<String>> get() = _meanings

    fun loadIdiom(idiom: IdiomEntity) {
        _uiState.value = ViewerState.Loading
        _isLiked.value = idiom.liked

        _title.value = idiom.title.toString()

        _meanings.value = cleanMeaning(idiom.meaning).split("|")

        _uiState.value = ViewerState.Loaded
    }

    fun likeIdiom(idiom: IdiomEntity) {
        viewModelScope.launch {
            val updatedIdiom = idiom.copy(liked = !idiom.liked)
            idiomRepo.updateIdiom(updatedIdiom)
            _isLiked.value = updatedIdiom.liked
        }
    }
}