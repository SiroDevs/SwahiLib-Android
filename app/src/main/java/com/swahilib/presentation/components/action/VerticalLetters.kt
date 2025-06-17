package com.swahilib.presentation.components.action

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun VerticalLetters(
    selectedLetter: String,
    onLetterSelected: (String) -> Unit
) {
    val letters = remember {
        ('A'..'Z')
            .map { it.toString() }
            .filter { it != "Q" && it != "X" }
    }

    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(letters) { letter ->
            LetterItem(
                text = letter,
                isSelected = selectedLetter == letter,
                onClick = { onLetterSelected(letter) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LetterItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) ThemeColors.primary else Color.White
    val contentColor = if (isSelected) Color.White else ThemeColors.primary

    Surface(
        modifier = Modifier
            .size(60.dp)
            .combinedClickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        color = backgroundColor,
        border = BorderStroke(
            width = if (isSelected) 0.dp else 1.dp,
            color = ThemeColors.primary.copy(alpha = 0.5f)
        ),
        shadowElevation = 5.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = contentColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerticalLettersPreview() {
    VerticalLetters(
        selectedLetter = "A",
        onLetterSelected = {}
    )
}
