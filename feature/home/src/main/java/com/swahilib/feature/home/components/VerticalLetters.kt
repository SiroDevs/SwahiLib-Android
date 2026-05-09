package com.swahilib.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerticalLetters(
    selectedLetter: String,
    onLetterSelected: (String) -> Unit,
) {
    val letters = remember {
        ('A'..'Z')
            .map { it.toString() }
            .filter { it != "Q" && it != "X" }
    }

    val scrollState = rememberLazyListState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .width(75.dp)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { LetterItem() }
            item { LetterItem() }

            items(letters) { letter ->
                LetterItem(
                    text = letter,
                    isSelected = selectedLetter == letter,
                    onClick = { onLetterSelected(letter) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LetterItem(
    text: String = "",
    isSelected: Boolean = false,
    onClick: () -> Unit = { },
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary

    if (text.isEmpty()) {
        Box( modifier = Modifier.size(45.dp) )
    } else {
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
}

@Preview(showBackground = true)
@Composable
fun VerticalLettersPreview() {
    VerticalLetters(
        selectedLetter = "A",
        onLetterSelected = {},
    )
}
