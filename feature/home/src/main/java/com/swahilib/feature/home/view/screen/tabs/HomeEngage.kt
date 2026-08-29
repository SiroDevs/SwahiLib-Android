package com.swahilib.feature.home.view.screen.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.TileAction
import com.swahilib.core.ui.components.action.TileActionCard
import com.swahilib.feature.home.view.components.FeaturedCard

private val gameTiles = listOf(
    TileAction("Jenga Maneno", Icons.Default.Extension, Routes.wordBuilderFreeplay()),
    TileAction("Jenga Sentensi", Icons.AutoMirrored.Filled.Sort, Routes.sentenceBuilderFreeplay()),
    TileAction("Tahajia (Spellcheck)", Icons.Default.Spellcheck, Routes.spellingFreeplay()),
    TileAction("CrossWord", Icons.Default.GridView, Routes.crosswordFreeplay()),
    TileAction("Sudoku", Icons.Default.ManageSearch, Routes.wordSearchFreeplay()),
    TileAction("Hangman", Icons.Default.AccessibilityNew, Routes.hangmanFreeplay()),
)

@Composable
fun HomeEngage(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeaturedCard(
                icon = Icons.Default.Quiz,
                title = "Jaribio la Msamiati",
                subtitle = "Mchezo wa Maswali (Quiz Free Play)",
                onClick = { navController.navigate(Routes.quizFreeplay()) }
            )

            Text(
                "MICHEZO YA KUKUSISIMUA",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            gameTiles.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    row.forEach { tile ->
                        TileActionCard(
                            tile = tile,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(tile.route) },
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { navController.navigate(Routes.PROGRESS) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = "Maendeleo Yangu",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Maendeleo Yangu",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
