package com.swahilib.feature.saying.view

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
import com.swahilib.core.database.model.SayingEntity
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.*
import com.swahilib.feature.saying.SayingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SayingScreen(
    navController: NavHostController,
    viewModel: SayingViewModel,
    saying: SayingEntity?,
) {
    val context = LocalContext.current
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()

    LaunchedEffect(saying) { saying?.let { viewModel.loadSaying(it) } }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Msemo wa Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        saying?.let {
                            viewModel.likeSaying(it)
                            val msg = if (!isLiked) "Msemo umeongezwa kwa vipendwa" else "Msemo umeondolewa kwa vipendwa"
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
                is ViewerState.Error -> ErrorState(message = (viewerState as ViewerState.Error).message, onRetry = { })
                ViewerState.Loaded -> SayingView(title = title, meanings = meanings)
                ViewerState.Loading -> LoadingState(title = "Subiri kidogo ...", fileName = "opener-loading")
                else -> EmptyState()
            }
        }
    }
}
