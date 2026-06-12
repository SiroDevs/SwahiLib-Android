package com.swahilib.feature.saying.view

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
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.core.ui.components.indicators.LoadingState
import com.swahilib.core.ui.components.share.ScreenshotReminderDialog
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.core.ui.components.share.ShareFab
import com.swahilib.core.ui.components.share.ShareSheet
import com.swahilib.feature.saying.SayingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SayingScreen(
    navController: NavHostController,
    viewModel: SayingViewModel,
    saying: SayingEntity?,
    prefsRepo: PrefsRepo,
) {
    val context = LocalContext.current
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val showDonation = remember { prefsRepo.shouldShowDonation() }

    var showShareSheet by remember { mutableStateOf(false) }
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(saying) { saying?.let { viewModel.loadSaying(it) } }

    val shareData = remember(title, meanings) {
        if (title.isNotBlank() && meanings.isNotEmpty()) {
            ShareData(
                emoji = "✨",
                typeLabel = "Msemo",
                title = title,
                meaning = meanings.random().trim(),
            )
        } else null
    }

    if (viewerState == ViewerState.Loaded) {
        ScreenshotReminderDialog(onShareClick = { showShareSheet = true })
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Msemo wa Kiswahili",
                tagline = "SwahiLib · Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        saying?.let {
                            viewModel.likeSaying(it)
                            val msg = if (!isLiked) "Msemo umeongezwa kwa vipendwa"
                            else "Msemo umeondolewa kwa vipendwa"
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
                ViewerState.Loaded -> SayingView(
                    title = title,
                    meanings = meanings,
                    showDonation = showDonation,
                    onShowDonation = { navController.navigate(Routes.DONATION) },
                )
                ViewerState.Loading -> LoadingState(title = "Subiri kidogo ...", fileName = "opener-loading")
                else -> EmptyState()
            }
        }

        if (showShareSheet && shareData != null) {
            ShareSheet(shareData = shareData, sheetState = shareSheetState, onDismiss = { showShareSheet = false })
        }
    }
}
