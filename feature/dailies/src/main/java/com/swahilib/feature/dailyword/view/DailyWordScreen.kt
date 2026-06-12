package com.swahilib.feature.dailies.view

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
import com.swahilib.core.database.model.WordEntity
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.share.ScreenshotReminderDialog
import com.swahilib.core.ui.components.share.ShareData
import com.swahilib.core.ui.components.share.ShareFab
import com.swahilib.core.ui.components.share.ShareSheet
import com.swahilib.feature.dailies.DailyWordViewModel
import com.swahilib.feature.word.WordViewModel
import com.swahilib.feature.word.view.WordScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWordScreen(
    navController: NavHostController,
    prefsRepo: PrefsRepo,
    viewModel: DailyWordViewModel = hiltViewModel(),
    wordViewModel: WordViewModel = hiltViewModel(),
) {
    var word by remember { mutableStateOf<WordEntity?>(null) }
    var loading by remember { mutableStateOf(true) }
    var showFullInfo by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }

    val fullInfoSheetState = rememberModalBottomSheetState(skipPartialExpansion = true)
    val shareSheetState = rememberModalBottomSheetState(skipPartialExpansion = true)

    LaunchedEffect(Unit) {
        word = viewModel.getRandomWord()
        loading = false
    }

    // Stable single random meaning for this session
    val singleMeaning = remember(word) {
        word?.meaning
            ?.split("|")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.randomOrNull() ?: ""
    }

    val shareData = remember(word, singleMeaning) {
        word?.let {
            ShareData(
                emoji = "📖",
                typeLabel = "Neno",
                title = it.title ?: "",
                meaning = singleMeaning,
            )
        }
    }

    if (word != null) ScreenshotReminderDialog(onShareClick = { showShareSheet = true })

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Neno la Siku",
                tagline = "SwahiLib · Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            if (word != null) ShareFab(onClick = { showShareSheet = true })
        },
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                loading -> CircularProgressIndicator()
                word == null -> Text("Hakuna neno lililopatikana.", style = MaterialTheme.typography.bodyLarge)
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // ── Hero card ──
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
                            Text("📖", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = word!!.title ?: "",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            if (!word!!.english.isNullOrBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = word!!.english!!,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                )
                            }
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

                    // ── Conjugation ──
                    val conj = word!!.conjugation?.replace("null", "")?.trim() ?: ""
                    if (conj.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "MNYAMBULIKO",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    letterSpacing = 1.5.sp,
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    conj,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
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
                WordScreen(
                    navController = navController,
                    viewModel = wordViewModel,
                    word = word,
                    prefsRepo = prefsRepo,
                )
            }
        }

        if (showShareSheet && shareData != null) {
            ShareSheet(shareData = shareData, sheetState = shareSheetState, onDismiss = { showShareSheet = false })
        }
    }
}
