package com.swahilib.presentation.screens.viewer.word

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.data.models.Word
import com.swahilib.domain.entity.ViewerState
import com.swahilib.presentation.components.indicators.LoadingState
import com.swahilib.presentation.components.action.AppTopBar
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScreen(
    navController: NavHostController,
    viewModel: WordViewModel,
    word: Word?,
) {
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val conjugation by viewModel.conjugation.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val synonyms by viewModel.synonyms.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()

    LaunchedEffect(word) {
        word?.let { viewModel.loadWord(it) }
    }

    Scaffold(topBar = {
        Surface(shadowElevation = 3.dp) {
            AppTopBar(
                title = "Kamusi ya Kiswahili",
                actions = {
//                    IconButton(onClick = {
//                        word?.let {
//                            viewModel.likeWord(it)
//
//                            val text = if (isLiked) {
//                                "Neno: ${word.title} limeongezwa kwa vipendwa"
//                            } else {
//                                "Neno: ${word.title} limeondolewa kwa vipendwa"
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
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
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
                    WordView(
                        viewModel = viewModel,
                        title = title,
                        conjugation = conjugation,
                        meanings = meanings,
                        synonyms = synonyms
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
