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

package com.swahilib.feature.daily_content.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.database.entities.content.WordEntity
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.feature.daily_content.view.components.DailyContentScaffold
import com.swahilib.feature.daily_content.viewmodel.DailyContentViewModel
import com.swahilib.feature.word.view.WordScreen
import com.swahilib.feature.word.viewmodel.WordViewModel

@Composable
fun DailyWordScreen(
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    viewModel: DailyContentViewModel = hiltViewModel(),
    wordViewModel: WordViewModel = hiltViewModel(),
) {
    var word by remember { mutableStateOf<WordEntity?>(null) }
    var dailyMeaning by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var streak by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val (dailyWord, meaning) = viewModel.getDailyWord()
        word = dailyWord
        dailyMeaning = meaning
        loading = false
        if (dailyWord != null) streak = prefsRepo.recordDailyVisit()
    }

    val shareData = remember(word, dailyMeaning) {
        word?.let {
            ShareData(
                emoji = "\ud83d\udcd6",
                headerLabel = "Neno la Kiswahili",
                title = it.title ?: "",
                meaning = dailyMeaning,
                english = it.english?.takeIf { e -> e.isNotBlank() },
            )
        }
    }

    DailyContentScaffold(
        title = "Neno la Siku",
        onBack = { navController.popBackStack() },
        onHistoryClick = { navController.navigate(Routes.dailyContentHistory(Routes.DAILY_CONTENT_TYPE_WORD)) },
        loading = loading,
        itemPresent = word != null,
        emptyMessage = "Hakuna neno lililopatikana.",
        prefsRepo = prefsRepo,
        onGoToNotificationSettings = { navController.navigate(Routes.SETTINGS) },
        streak = streak,
        singleMeaning = dailyMeaning,
        shareData = shareData,
        heroCard = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(4.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("\ud83d\udcd6", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = word?.title ?: "",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    if (!word?.english.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = word?.english!!,
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        },
        extraCard = {
            val conj = word?.conjugation?.replace("null", "")?.trim() ?: ""
            if (conj.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "MNYAMBULIKO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            letterSpacing = 1.5.sp,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            conj,
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        },
        fullInfoContent = {
            WordScreen(
                navController = navController,
                viewModel = wordViewModel,
                word = word,
                prefsRepo = prefsRepo,
            )
        },
    )
}
