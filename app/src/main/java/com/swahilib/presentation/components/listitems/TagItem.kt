package com.swahilib.presentation.components.listitems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun TagView(tagText: String) {
    if (tagText.isNotEmpty()) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isSystemInDarkTheme()) ThemeColors.primary2 else ThemeColors.primary,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = tagText,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    //fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTagView() {
    TagView(tagText = "Chorus")
}