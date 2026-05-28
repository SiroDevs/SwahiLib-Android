package com.swahilib.feature.idiom.view

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
import com.swahilib.core.database.model.IdiomEntity
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.*
import com.swahilib.feature.idiom.IdiomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdiomScreen(
    navController: NavHostController,
    viewModel: IdiomViewModel,
    idiom: IdiomEntity?,
) {
    val context = LocalContext.current
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()

    LaunchedEffect(idiom) { idiom?.let { viewModel.loadIdiom(it) } }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Nahau ya Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        idiom?.let {
                            viewModel.likeIdiom(it)
                            val msg = if (!isLiked) "Nahau imeongezwa kwa vipendwa" else "Nahau imeondolewa kwa vipendwa"
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
                ViewerState.Loaded -> IdiomView(title = title, meanings = meanings)
                ViewerState.Loading -> LoadingState(title = "Subiri kidogo ...", fileName = "opener-loading")
                else -> EmptyState()
            }
        }
    }
}
