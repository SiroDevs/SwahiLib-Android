package com.swahilib.presentation.components.listitems

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun SearchBookItem(
    text: String,
    isSelected: Boolean = false,
    onPressed: (() -> Unit)? = null
) {
    val backgroundColor = if (isSelected) ThemeColors.primary2 else ThemeColors.accent
    val contentColor = if (isSelected) Color.White else Color.Black

    Button(
        onClick = { onPressed?.invoke() },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchBookItem() {
    SearchBookItem(
        text = "Song of Worship",
        isSelected = false,
        onPressed = {}
    )
}