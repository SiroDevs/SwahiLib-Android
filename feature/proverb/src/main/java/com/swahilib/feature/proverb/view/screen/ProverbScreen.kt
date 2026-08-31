package com.swahilib.feature.proverb.view.screen

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
import com.swahilib.core.database.entities.content.ProverbEntity
import com.swahilib.core.ui.components.content.ContentDetailScaffold
import com.swahilib.core.ui.components.share.ShareData
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

    var selectedMeaning by remember(title) { mutableStateOf<String?>(null) }

    LaunchedEffect(proverb) { proverb?.let { viewModel.loadProverb(it) } }

    val shareData = remember(title, selectedMeaning, meanings, synonyms) {
        val meaning = selectedMeaning ?: meanings.singleOrNull()?.trim()
        if (title.isNotBlank() && meaning != null) {
            ShareData(
                emoji = "\ud83c\udf3f",
                headerLabel = "Methali ya Kiswahili",
                title = title,
                meaning = meaning,
                synonyms = synonyms.mapNotNull { it.title },
            )
        } else null
    }

    ContentDetailScaffold(
        title = "Methali ya Kiswahili",
        viewerState = viewerState,
        onBack = { navController.popBackStack() },
        isLiked = isLiked,
        onToggleLike = {
            proverb?.let {
                viewModel.likeProverb(it)
                val msg = if (!isLiked) "Methali imeongezwa kwa vipendwa"
                else "Methali imeondolewa kutoka kwa vipendwa"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        },
        meanings = meanings,
        shareData = shareData,
        onSelectMeaning = { selectedMeaning = it },
    ) {
        ProverbDetails(
            viewModel = viewModel,
            title = title,
            meanings = meanings,
            synonyms = synonyms,
            explanations = explanations,
            showDonation = showDonation,
            onShowDonation = { navController.navigate(Routes.DONATION) },
        )
    }
}
