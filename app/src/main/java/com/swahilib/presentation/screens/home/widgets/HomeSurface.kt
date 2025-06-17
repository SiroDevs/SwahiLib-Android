package com.swahilib.presentation.screens.home.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.data.sample.SampleWords
import com.swahilib.presentation.components.listitems.WordItem
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun HomeSurface(
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 25.dp)),
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .background(ThemeColors.accent,
//                    brush = Brush.radialGradient(
//                        colors = listOf(
//                            Color.White,
//                            Color(0xFF64B5F6).copy(alpha = 0.2f),
//                            Color(0xFF1976D2).copy(alpha = 0.4f),
//                            Color(0xFF0D47A1).copy(alpha = 0.7f)
//                        ),
//                        radius = 0.8f,
//                    )
                )
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeSurfacePreview() {
    HomeSurface() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 5.dp),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            items(SampleWords) { word ->
                WordItem(
                    word = word,
                    onTap = { },
                )
            }
        }
    }
}