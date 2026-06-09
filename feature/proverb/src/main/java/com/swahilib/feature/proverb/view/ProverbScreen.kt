package com.swahilib.feature.proverb.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.*
import com.swahilib.feature.proverb.ProverbViewModel

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

    LaunchedEffect(proverb) { proverb?.let { viewModel.loadProverb(it) } }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Methali ya Kiswahili",
                tagline = "SwahiLib - Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        proverb?.let {
                            viewModel.likeProverb(it)
                            val msg =
                                if (!isLiked) "Methali imeongezwa kwa vipendwa" else "Methali imeondolewa kwa vipendwa"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Penda",
                            tint = if (isLiked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (viewerState) {
                is ViewerState.Error -> ErrorState(
                    message = (viewerState as ViewerState.Error).message,
                    onRetry = { })

                ViewerState.Loaded -> ProverbDetails(
                    viewModel = viewModel,
                    title = title,
                    meanings = meanings,
                    synonyms = synonyms,
                    explanations = explanations,
                    showDonation = showDonation,
                    onShowDonation = { navController.navigate(Routes.DONATION) },
                )

                ViewerState.Loading -> LoadingState(
                    title = "Subiri kidogo ...",
                    fileName = "opener-loading"
                )

                else -> EmptyState()
            }
        }
    }
}
