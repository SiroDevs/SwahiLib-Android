package com.swahilib.feature.idiom.view

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
import com.swahilib.core.database.entities.content.IdiomEntity
import com.swahilib.core.ui.components.content.ContentDetailScaffold
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.feature.idiom.viewmodel.IdiomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdiomScreen(
    navController: NavHostController,
    viewModel: IdiomViewModel,
    idiom: IdiomEntity?,
    prefsRepo: PrefsRepo,
) {
    val context = LocalContext.current
    val viewerState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val meanings by viewModel.meanings.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val showDonation = remember { prefsRepo.shouldShowDonation() }

    var selectedMeaning by remember(title) { mutableStateOf<String?>(null) }

    LaunchedEffect(idiom) { idiom?.let { viewModel.loadIdiom(it) } }

    val shareData = remember(title, selectedMeaning, meanings) {
        val meaning = selectedMeaning ?: meanings.singleOrNull()?.trim()
        if (title.isNotBlank() && meaning != null) {
            ShareData(
                emoji = "\ud83d\udcac",
                headerLabel = "Nahau ya Kiswahili",
                title = title,
                meaning = meaning,
            )
        } else null
    }

    ContentDetailScaffold(
        title = "Nahau ya Kiswahili",
        viewerState = viewerState,
        onBack = { navController.popBackStack() },
        isLiked = isLiked,
        onToggleLike = {
            idiom?.let {
                viewModel.likeIdiom(it)
                val msg = if (!isLiked) "Nahau imeongezwa kwa vipendwa"
                else "Nahau imeondolewa kutoka kwa vipendwa"
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        },
        meanings = meanings,
        shareData = shareData,
        onSelectMeaning = { selectedMeaning = it },
    ) {
        IdiomView(
            title = title,
            meanings = meanings,
            showDonation = showDonation,
            onShowDonation = { navController.navigate(Routes.DONATION) },
        )
    }
}
