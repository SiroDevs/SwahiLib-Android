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
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.feature.daily_content.view.components.DailyContentScaffold
import com.swahilib.feature.daily_content.viewmodel.DailyContentViewModel
import com.swahilib.feature.proverb.view.screen.ProverbScreen
import com.swahilib.feature.proverb.viewmodel.ProverbViewModel

@Composable
fun DailyProverbScreen(
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    viewModel: DailyContentViewModel = hiltViewModel(),
    proverbViewModel: ProverbViewModel = hiltViewModel(),
) {
    var proverb by remember { mutableStateOf<ProverbEntity?>(null) }
    var dailyMeaning by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var streak by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val (dailyProverb, meaning) = viewModel.getDailyProverb()
        proverb = dailyProverb
        dailyMeaning = meaning
        loading = false
        // Viewing the daily proverb also counts toward the same daily streak as
        // the daily word - either one is the "showed up today" signal.
        if (dailyProverb != null) streak = prefsRepo.recordDailyVisit()
    }

    val shareData = remember(proverb, dailyMeaning) {
        proverb?.let {
            ShareData(
                emoji = "\ud83c\udf3f",
                headerLabel = "Methali ya Kiswahili",
                title = it.title ?: "",
                meaning = dailyMeaning,
            )
        }
    }

    DailyContentScaffold(
        title = "Methali ya Siku",
        onBack = { navController.popBackStack() },
        onHistoryClick = { navController.navigate(Routes.dailyContentHistory(Routes.DAILY_CONTENT_TYPE_PROVERB)) },
        loading = loading,
        itemPresent = proverb != null,
        emptyMessage = "Hakuna methali iliyopatikana.",
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
                    Text("\ud83c\udf3f", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "\"${proverb?.title}\"",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        },
        fullInfoContent = {
            ProverbScreen(
                navController = navController,
                viewModel = proverbViewModel,
                proverb = proverb,
                prefsRepo = prefsRepo,
            )
        },
    )
}
