package com.swahilib.presentation.theme

import androidx.compose.ui.graphics.Color

object ThemeColors {
    val accent = Color(0xFFECECEC);
    val accent1 = Color(0xFFEEDAC4)
    val accent2 = Color(0xFFBEA264)
    val accent3 = Color(0xFFFFB74D)
    val primary = Color(0xFFB86918)
    val primary1 = Color(0xFFF57C00)
    val primary2 = Color(0xFFBF360C)
    val primaryDark = Color(0xFF432B25)
    val primaryDark1 = Color(0xFF331900)

    // Optional helper for light/dark background color
    fun bgColorWB(isLight: Boolean): Color {
        return if (isLight) Color.White else primaryDark
    }

    // Optional Compose helper if you want to pass Compose theme directly
//    @Composable
//    fun bgColorWB(): Color {
//        return if (MaterialTheme.colorScheme.isLight) white else primaryDark
//    }
}
