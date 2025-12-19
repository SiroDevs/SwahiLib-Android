package com.swahilib.presentation.home.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

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
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .size(60.dp)
            .combinedClickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        color = backgroundColor,
        border = BorderStroke(
            width = if (isSelected) 0.dp else 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
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
