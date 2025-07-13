package com.swahilib.presentation.screens.viewer.idiom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.data.models.Idiom
import com.swahilib.domain.entities.ViewerState
import com.swahilib.presentation.components.indicators.LoadingState
import com.swahilib.presentation.components.action.AppTopBar
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.IdiomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdiomScreen(
    onBackPressed: () -> Unit,
    viewModel: IdiomViewModel,
    idiom: Idiom?,
) {
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()

    LaunchedEffect(idiom) {
        idiom?.let { viewModel.loadIdiom(it) }
    }

    Scaffold(topBar = {
        Surface(shadowElevation = 3.dp) {
            AppTopBar(
                title = "Kamusi ya Kiswahili",
                actions = {
//                    IconButton(onClick = {
//                        idiom?.let {
//                            viewModel.likeIdiom(it)
//
//                            val text = if (isLiked) {
//                                "Neno: ${idiom.title} limeongezwa kwa vipendwa"
//                            } else {
//                                "Neno: ${idiom.title} limeondolewa kwa vipendwa"
//                            }
//                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
//                        }
//                    }) {
//                        Icon(
//                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
//                            contentDescription = "Penda"
//                        )
//                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                shadowElevation = 0.dp,
            )
        }
    }, content = {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(ThemeColors.accent2)
        ) {
            when (viewerState) {
                is ViewerState.Error -> ErrorState(
                    message = (viewerState as ViewerState.Error).message, onRetry = { })

                ViewerState.Loaded -> {
                    IdiomView(
                        title = title,
                        meanings = meanings,
                    )
                }

                ViewerState.Loading -> LoadingState(
                    title = "Subiri kidogo ...",
                    fileName = "opener-loading",
                )

                else -> EmptyState()
            }
        }
    })
}
