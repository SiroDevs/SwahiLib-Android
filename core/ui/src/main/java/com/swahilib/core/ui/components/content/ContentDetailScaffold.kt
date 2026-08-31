package com.swahilib.core.ui.components.content

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.core.ui.components.share.MeaningPickerDialog
import com.swahilib.core.ui.components.share.ScreenshotReminderDialog
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.core.ui.components.share.ShareFab
import com.swahilib.core.ui.components.share.ShareSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentDetailScaffold(
    title: String,
    viewerState: ViewerState,
    onBack: () -> Unit,
    isLiked: Boolean,
    onToggleLike: () -> Unit,
    meanings: List<String>,
    shareData: ShareData?,
    onSelectMeaning: (String) -> Unit,
    modifier: Modifier = Modifier,
    onErrorRetry: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var showShareSheet by remember { mutableStateOf(false) }
    var showMeaningPicker by remember { mutableStateOf(false) }
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // More than one meaning: ask which one to share, otherwise there's only
    // one option so go straight to the share sheet.
    val requestShare = { if (meanings.size > 1) showMeaningPicker = true else showShareSheet = true }

    if (viewerState == ViewerState.Loaded) {
        ScreenshotReminderDialog(onShareClick = requestShare)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = title,
                tagline = "SwahiLib \u00b7 Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = onBack,
                actions = {
                    IconButton(onClick = onToggleLike) {
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
                ShareFab(onClick = requestShare)
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
                is ViewerState.Error -> ErrorState(message = viewerState.message, onRetry = onErrorRetry)
                ViewerState.Loaded -> content()
                ViewerState.Loading -> {}
                else -> EmptyState()
            }

            if (showMeaningPicker) {
                MeaningPickerDialog(
                    meanings = meanings,
                    onSelect = {
                        onSelectMeaning(it)
                        showMeaningPicker = false
                        showShareSheet = true
                    },
                    onDismiss = { showMeaningPicker = false },
                )
            }

            if (showShareSheet && shareData != null) {
                ShareSheet(shareData = shareData, sheetState = shareSheetState, onDismiss = { showShareSheet = false })
            }
        }
    }
}
