package com.swahilib.feature.daily_content.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.core.database.model.ProverbEntity
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.general.NotificationReminderBanner
import com.swahilib.core.ui.components.share.ScreenshotReminderDialog
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.core.ui.components.share.ShareFab
import com.swahilib.core.ui.components.share.ShareSheet
import com.swahilib.feature.dailies.viewmodel.DailyContentViewModel
import com.swahilib.feature.proverb.viewmodel.ProverbViewModel
import com.swahilib.feature.proverb.view.screen.ProverbScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyProverbScreen(
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    viewModel: DailyContentViewModel = hiltViewModel(),
    proverbViewModel: ProverbViewModel = hiltViewModel(),
) {
    var proverb by remember { mutableStateOf<ProverbEntity?>(null) }
    var dailyMeaning by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var showFullInfo by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }

    val fullInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shareSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        val (dailyProverb, meaning) = viewModel.getDailyProverb()
        proverb = dailyProverb
        dailyMeaning = meaning
        loading = false
    }

    val singleMeaning = dailyMeaning

    val shareData = remember(proverb, singleMeaning) {
        proverb?.let {
            ShareData(
                emoji = "🌿",
                typeLabel = "Methali",
                title = it.title ?: "",
                meaning = singleMeaning,
                textToShare = "\"${it.title}\"\n\n$singleMeaning\n\n— SwahiLib · Kamusi ya Kiswahili",
            )
        }
    }

    if (proverb != null) ScreenshotReminderDialog(onShareClick = { showShareSheet = true })

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Methali ya Siku",
                tagline = "SwahiLib · Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            if (proverb != null) ShareFab(onClick = { showShareSheet = true })
        },
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                loading -> CircularProgressIndicator()
                proverb == null -> Text("Hakuna methali iliyopatikana.", style = MaterialTheme.typography.bodyLarge)
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // ── Notification reminder banner ──
                    NotificationReminderBanner(
                        prefsRepo = prefsRepo,
                        onGoToSettings = { navController.navigate(com.swahilib.core.common.utils.Routes.SETTINGS) },
                        modifier = Modifier.padding(horizontal = 0.dp),
                    )
                    // ── Proverb hero card ──
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = CardDefaults.cardElevation(4.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("🌿", fontSize = 40.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "\"${proverb!!.title}\"",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    // ── One random meaning ──
                    if (singleMeaning.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "MAANA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.5.sp,
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(singleMeaning, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    // ── More info button ──
                    Button(
                        onClick = { showFullInfo = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) { Text("Tazama Maelezo Zaidi") }
                }
            }
        }

        if (showFullInfo) {
            ModalBottomSheet(
                onDismissRequest = { showFullInfo = false },
                sheetState = fullInfoSheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = { BottomSheetDefaults.DragHandle() },
            ) {
                ProverbScreen(
                    navController = navController,
                    viewModel = proverbViewModel,
                    proverb = proverb,
                    prefsRepo = prefsRepo,
                )
            }
        }

        if (showShareSheet && shareData != null) {
            ShareSheet(shareData = shareData, sheetState = shareSheetState, onDismiss = { showShareSheet = false })
        }
    }
}
