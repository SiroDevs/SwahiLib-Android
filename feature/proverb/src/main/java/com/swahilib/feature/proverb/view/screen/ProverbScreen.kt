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

package com.swahilib.feature.proverb.view.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.core.ui.components.share.ScreenshotReminderDialog
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.core.ui.components.share.ShareFab
import com.swahilib.core.ui.components.share.ShareSheet
import com.swahilib.feature.proverb.viewmodel.ProverbViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProverbScreen(
    navController: NavHostController,
    viewModel: ProverbViewModel,
    proverb: ProverbEntity?,
    prefsRepo: PrefsRepo,
) {
    val context = LocalContext.current
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val synonyms by viewModel.synonyms.collectAsState()
    val explanations by viewModel.explanations.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val showDonation = remember { prefsRepo.shouldShowDonation() }

    var showShareSheet by remember { mutableStateOf(false) }
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(proverb) { proverb?.let { viewModel.loadProverb(it) } }

    val shareData = remember(title, meanings) {
        if (title.isNotBlank() && meanings.isNotEmpty()) {
            ShareData(
                emoji = "🌿",
                typeLabel = "Methali",
                title = title,
                meaning = meanings.random().trim(),
                textToShare = "\"$title\"\n\n${meanings.random().trim()}\n\n— SwahiLib · Kamusi ya Kiswahili",
            )
        } else null
    }

    if (viewerState == ViewerState.Loaded) {
        ScreenshotReminderDialog(onShareClick = { showShareSheet = true })
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Methali ya Kiswahili",
                tagline = "SwahiLib · Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        proverb?.let {
                            viewModel.likeProverb(it)
                            val msg = if (!isLiked) "Methali imeongezwa kwa vipendwa"
                            else "Methali imeondolewa kwa vipendwa"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Penda",
                            tint = if (isLiked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (viewerState == ViewerState.Loaded && shareData != null) {
                ShareFab(onClick = { showShareSheet = true })
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            when (viewerState) {
                is ViewerState.Error -> ErrorState(
                    message = (viewerState as ViewerState.Error).message,
                    onRetry = {},
                )
                ViewerState.Loaded -> ProverbDetails(
                    viewModel = viewModel,
                    title = title,
                    meanings = meanings,
                    synonyms = synonyms,
                    explanations = explanations,
                    showDonation = showDonation,
                    onShowDonation = { navController.navigate(Routes.DONATION) },
                )
                ViewerState.Loading -> {}
                else -> EmptyState()
            }
        }

        if (showShareSheet && shareData != null) {
            ShareSheet(shareData = shareData, sheetState = shareSheetState, onDismiss = { showShareSheet = false })
        }
    }
}
