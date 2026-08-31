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

package com.swahilib.feature.daily_content.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.NotificationReminderBanner
import com.swahilib.core.ui.components.general.StreakBadge
import com.swahilib.core.ui.components.share.ScreenshotReminderDialog
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.core.ui.components.share.ShareFab
import com.swahilib.core.ui.components.share.ShareSheet

/**
 * The scaffold shared by every "daily content" screen (Daily Word, Daily
 * Proverb): top bar with a history icon, loading/empty/loaded states,
 * notification banner, streak badge, hero card slot, the standard "MAANA"
 * card, an optional extra card (e.g. Daily Word's conjugation card), the
 * "Tazama Maelezo Zaidi" bottom sheet, and the share flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyContentScaffold(
    title: String,
    onBack: () -> Unit,
    onHistoryClick: () -> Unit,
    loading: Boolean,
    itemPresent: Boolean,
    emptyMessage: String,
    prefsRepo: PrefsRepo,
    onGoToNotificationSettings: () -> Unit,
    streak: Int,
    singleMeaning: String,
    shareData: ShareData?,
    heroCard: @Composable () -> Unit,
    fullInfoContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    extraCard: (@Composable () -> Unit)? = null,
) {
    var showFullInfo by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }
    val fullInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (itemPresent) ScreenshotReminderDialog(onShareClick = { showShareSheet = true })

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = title,
                tagline = "SwahiLib \u00b7 Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = onBack,
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, contentDescription = "Historia")
                    }
                },
            )
        },
        floatingActionButton = {
            if (itemPresent) ShareFab(onClick = { showShareSheet = true })
        },
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                loading -> CircularProgressIndicator()
                !itemPresent -> Text(emptyMessage, style = MaterialTheme.typography.bodyLarge)
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    NotificationReminderBanner(
                        prefsRepo = prefsRepo,
                        onGoToSettings = onGoToNotificationSettings,
                        modifier = Modifier.padding(horizontal = 0.dp),
                    )
                    StreakBadge(streakCount = streak, modifier = Modifier.align(Alignment.CenterHorizontally))
                    heroCard()

                    if (singleMeaning.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "MAANA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.5.sp,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(singleMeaning, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    extraCard?.invoke()

                    Button(
                        onClick = { showFullInfo = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) { Text("Tazama Maelezo Zaidi") }
                }
            }
        }

        if (showFullInfo) {
            ModalBottomSheet(
                onDismissRequest = { showFullInfo = false },
                sheetState = fullInfoSheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = { BottomSheetDefaults.DragHandle() },
            ) {
                fullInfoContent()
            }
        }

        if (showShareSheet && shareData != null) {
            ShareSheet(shareData = shareData, sheetState = shareSheetState, onDismiss = { showShareSheet = false })
        }
    }
}
