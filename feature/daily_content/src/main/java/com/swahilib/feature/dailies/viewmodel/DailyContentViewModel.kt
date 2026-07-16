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

package com.swahilib.feature.dailies.viewmodel

import androidx.lifecycle.ViewModel
import com.swahilib.core.data.repos.DailyContentRepo
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.WordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DailyContentViewModel @Inject constructor(
    private val dailyContentRepo: DailyContentRepo,
) : ViewModel() {

    suspend fun getDailyWord(): Pair<WordEntity?, String> = dailyContentRepo.getDailyWord()

    suspend fun getDailyProverb(): Pair<ProverbEntity?, String> = dailyContentRepo.getDailyProverb()
}