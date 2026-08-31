package com.swahilib.feature.word.view

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
import com.swahilib.core.database.entities.content.WordEntity
import com.swahilib.core.ui.components.content.ContentDetailScaffold
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.feature.word.viewmodel.WordViewModel

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

    var selectedMeaning by remember(title) { mutableStateOf<String?>(null) }

    LaunchedEffect(word) { word?.let { viewModel.loadWord(it) } }

    val shareData = remember(title, selectedMeaning, meanings, english, synonyms) {
        val meaning = selectedMeaning ?: meanings.singleOrNull()?.trim()
        if (title.isNotBlank() && meaning != null) {
            ShareData(
                emoji = "\ud83d\udcd6",
                headerLabel = "Neno la Kiswahili",
                title = title,
                meaning = meaning,
                english = english,
                synonyms = synonyms.mapNotNull { it.title },
            )
        } else null
    }

    ContentDetailScaffold(
        title = "Neno la Kiswahili",
        viewerState = viewerState,
        onBack = { navController.popBackStack() },
        isLiked = isLiked,
        onToggleLike = {
            word?.let {
                viewModel.likeWord(it)
                val msg = if (!isLiked) "Neno limeongezwa kwa vipendwa"
                else "Neno limeondolewa kutoka kwa vipendwa"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        },
        meanings = meanings,
        shareData = shareData,
        onSelectMeaning = { selectedMeaning = it },
    ) {
        WordView(
            viewModel = viewModel,
            title = title,
            conjugation = conjugation,
            meanings = meanings,
            synonyms = synonyms,
            english = english,
            showDonation = showDonation,
            onShowDonation = { navController.navigate(Routes.DONATION) },
        )
    }
}
