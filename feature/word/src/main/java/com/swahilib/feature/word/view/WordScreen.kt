package com.swahilib.feature.word.view

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.ViewerState
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.core.ui.components.indicators.ErrorState
import com.swahilib.core.ui.components.indicators.LoadingState
import com.swahilib.feature.word.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordScreen(
    navController: NavHostController,
    viewModel: WordViewModel,
    word: WordEntity?,
    prefsRepo: PrefsRepo,
) {
    val context = LocalContext.current
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val conjugation by viewModel.conjugation.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val synonyms by viewModel.synonyms.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val english by viewModel.english.collectAsState()
    val showDonation = remember { prefsRepo.shouldShowDonation() }

    LaunchedEffect(word) { word?.let { viewModel.loadWord(it) } }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Neno la Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        word?.let {
                            viewModel.likeWord(it)
                            val msg = if (!isLiked) "Neno limeongezwa kwa vipendwa" else "Neno limeondolewa kwa vipendwa"
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
                    onRetry = { }
                )
                ViewerState.Loaded -> WordView(
                    viewModel = viewModel,
                    title = title,
                    conjugation = conjugation,
                    meanings = meanings,
                    synonyms = synonyms,
                    english = english,
                    showDonation = showDonation,
                    onShowDonation = { navController.navigate(Routes.DONATION) },
                )
                ViewerState.Loading -> LoadingState(title = "Subiri kidogo ...", fileName = "opener-loading")
                else -> EmptyState()
            }
        }
    }
}
