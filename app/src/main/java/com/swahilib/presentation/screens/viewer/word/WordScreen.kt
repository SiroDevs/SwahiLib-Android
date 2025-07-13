package com.swahilib.presentation.screens.viewer.word

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.swahilib.data.models.Word
import com.swahilib.domain.entities.ViewerState
import com.swahilib.presentation.components.indicators.LoadingState
import com.swahilib.presentation.components.action.AppTopBar
import com.swahilib.presentation.components.indicators.*
import com.swahilib.presentation.theme.ThemeColors
import com.swahilib.presentation.viewmodels.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScreen(
    onBackPressed: () -> Unit,
    viewModel: WordViewModel,
    word: Word?,
) {
    val context = LocalContext.current
    val viewerState by viewModel.uiState.collectAsState()
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
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
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

                ViewerState.Loaded -> word?.let { it1 ->
                    WordView(
                        word = it1,
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
