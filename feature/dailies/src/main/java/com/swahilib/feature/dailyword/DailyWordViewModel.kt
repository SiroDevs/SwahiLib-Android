package com.swahilib.feature.dailies

import androidx.lifecycle.ViewModel
import com.swahilib.core.data.repos.ProverbRepo
import com.swahilib.core.data.repos.WordRepo
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.database.model.WordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DailyWordViewModel @Inject constructor(
    private val wordRepo: WordRepo,
    private val proverbRepo: ProverbRepo,
) : ViewModel() {
    suspend fun getRandomWord(): WordEntity? = wordRepo.getRandomWord()
    suspend fun getRandomProverb(): ProverbEntity? = proverbRepo.getRandomProverb()
}
