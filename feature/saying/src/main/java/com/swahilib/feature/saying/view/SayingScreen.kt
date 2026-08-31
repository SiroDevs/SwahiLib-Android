package com.swahilib.feature.saying.view

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.utils.PrefsRepo
import com.swahilib.core.database.entities.content.SayingEntity
import com.swahilib.core.ui.components.content.ContentDetailScaffold
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.feature.saying.viewmodel.SayingViewModel

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

    var selectedMeaning by remember(title) { mutableStateOf<String?>(null) }

    LaunchedEffect(saying) { saying?.let { viewModel.loadSaying(it) } }

    val shareData = remember(title, selectedMeaning, meanings) {
        val meaning = selectedMeaning ?: meanings.singleOrNull()?.trim()
        if (title.isNotBlank() && meaning != null) {
            ShareData(
                emoji = "\u2728",
                headerLabel = "Msemo wa Kiswahili",
                title = title,
                meaning = meaning,
            )
        } else null
    }

    ContentDetailScaffold(
        title = "Msemo wa Kiswahili",
        viewerState = viewerState,
        onBack = { navController.popBackStack() },
        isLiked = isLiked,
        onToggleLike = {
            saying?.let {
                viewModel.likeSaying(it)
                val msg = if (!isLiked) "Msemo umeongezwa kwa vipendwa"
                else "Msemo umeondolewa kutoka kwa vipendwa"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        },
        meanings = meanings,
        shareData = shareData,
        onSelectMeaning = { selectedMeaning = it },
    ) {
        SayingView(
            title = title,
            meanings = meanings,
            showDonation = showDonation,
            onShowDonation = { navController.navigate(Routes.DONATION) },
        )
    }
}
