package com.swahilib.presentation.screens.viewer.word

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.swahilib.data.models.Word
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
//            item {
//                CollapsingHeader(title = word.title ?: "")
//            }
            item {
                if (!word.conjugation.isNullOrEmpty()) {
                    Text(
                        text = "Mnyambuliko: ${word.conjugation}",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
//                WordDetails(
//                    word = word,
//                    meanings = meanings,
//                    synonyms = synonyms
//                )
            }
        }
    }
}
