package com.swahilib.feature.daily_content.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.DailyContentHistoryEntry
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.core.ui.components.indicators.EmptyState
import com.swahilib.feature.daily_content.viewmodel.DailyContentViewModel

private val SWAHILI_MONTHS = listOf(
    "Januari", "Februari", "Machi", "Aprili", "Mei", "Juni",
    "Julai", "Agosti", "Septemba", "Oktoba", "Novemba", "Desemba",
)

private fun formatDailyDate(date: String): String {
    val parts = date.split("-")
    if (parts.size != 3) return date
    val (year, month, day) = parts
    val monthName = month.toIntOrNull()?.let { SWAHILI_MONTHS.getOrNull(it - 1) } ?: return date
    return "${day.toIntOrNull() ?: day} $monthName $year"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyContentHistory(
    navController: NavHostController,
    viewModel: DailyContentViewModel = hiltViewModel(),
    type: String,
) {
    val history by viewModel.history.collectAsState()
    val isWord = type != Routes.DAILY_CONTENT_TYPE_PROVERB

    LaunchedEffect(Unit) { viewModel.loadHistory() }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isWord) "Historia ya Maneno ya Siku" else "Historia ya Methali za Siku",
                tagline = "SwahiLib · Kamusi ya Kiswahili",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
    ) { padding ->
        if (history.isEmpty()) {
            EmptyState(
                title = "Bado Hakuna Historia",
                message = if (isWord) {
                    "Maneno ya siku yatajazwa hapa siku zinapopita."
                } else {
                    "Methali za siku zitajazwa hapa siku zinapopita."
                },
                messageIcon = Icons.Default.History,
            )
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(history, key = { it.date }) { entry ->
                    DailyHistoryRow(entry = entry, isWord = isWord)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun DailyHistoryRow(entry: DailyContentHistoryEntry, isWord: Boolean) {
    val title = if (isWord) entry.word?.title else entry.proverb?.title
    val meaning = if (isWord) entry.wordMeaning else entry.proverbMeaning

    ListItem(
        overlineContent = {
            Text(
                text = formatDailyDate(entry.date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        headlineContent = {
            Text(
                text = title ?: "—",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            if (meaning.isNotBlank()) {
                Text(
                    text = meaning,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
    )
}
