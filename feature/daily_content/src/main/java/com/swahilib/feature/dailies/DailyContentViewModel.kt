package com.swahilib.feature.daily_content

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

    /**
     * Today's Word of the Day plus the single meaning chosen for it.
     * Backed by [DailyContentRepo] so it's the exact same word (and meaning)
     * shown in the notification and the home-screen widget for the day.
     */
    suspend fun getDailyWord(): Pair<WordEntity?, String> = dailyContentRepo.getDailyWord()

    /**
     * Today's Proverb of the Day plus the single meaning chosen for it.
     * Backed by [DailyContentRepo] so it's the exact same proverb (and
     * meaning) shown in the notification and the home-screen widget for the day.
     */
    suspend fun getDailyProverb(): Pair<ProverbEntity?, String> = dailyContentRepo.getDailyProverb()
}
