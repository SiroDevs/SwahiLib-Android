package com.swahilib.presentation.screens.viewer.word

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.swahilib.data.models.Word
import com.swahilib.presentation.components.action.CollapsingHeader
import com.swahilib.presentation.components.general.MeaningsView
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun WordView(
    modifier: Modifier = Modifier,
    word: Word,
    meanings: List<String>,
    synonyms: List<String>
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.White, ThemeColors.accent1, ThemeColors.primary1)
                )
            )
    ) {
        LazyColumn(state = scrollState) {
            item {
                CollapsingHeader(title = word.title ?: "")
            }
            item {
                if (meanings.isNotEmpty()) {
                    MeaningsView(meanings = meanings)
                }
                if (!word.conjugation.isNullOrEmpty()) {
                    Text(
                        text = "Mnyambuliko: ${word.conjugation}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                if (synonyms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Visawe",
                        fontSize = 25.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = ThemeColors.primary1
                    )
                    synonyms.forEach { synonym ->
                        WordSynonym(synonym = synonym)
                    }
                }
            }
        }
    }
}

@Composable
fun WordSynonym(synonym: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowCircleRight, contentDescription = null, tint = ThemeColors.primary1)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = synonym,
                fontSize = 20.sp,
                color = ThemeColors.primary1
            )
        }
    }
}
