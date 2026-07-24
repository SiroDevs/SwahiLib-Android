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