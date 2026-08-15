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
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.core.ui.components.share.MeaningPickerDialog
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
    var showMeaningPicker by remember { mutableStateOf(false) }
    var selectedMeaning by remember(title) { mutableStateOf<String?>(null) }
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(proverb) { proverb?.let { viewModel.loadProverb(it) } }

    val shareData = remember(title, selectedMeaning, meanings, synonyms) {
        val meaning = selectedMeaning ?: meanings.singleOrNull()?.trim()
        if (title.isNotBlank() && meaning != null) {
            ShareData(
                emoji = "🌿",
                headerLabel = "Methali ya Kiswahili",
                title = title,
                meaning = meaning,
                synonyms = synonyms.mapNotNull { it.title },
            )
        } else null
    }

    val requestShare = {
        if (meanings.size > 1) showMeaningPicker = true else showShareSheet = true
    }

    if (viewerState == ViewerState.Loaded) {
        ScreenshotReminderDialog(onShareClick = requestShare)
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
                            else "Methali imeondolewa kutoka kwa vipendwa"
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

        if (showMeaningPicker) {
            MeaningPickerDialog(
                meanings = meanings,
                onSelect = {
                    selectedMeaning = it
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
