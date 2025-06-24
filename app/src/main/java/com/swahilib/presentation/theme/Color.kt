package com.swahilib.presentation.theme

import androidx.compose.ui.graphics.Color

object ThemeColors {
    val accent1 = Color(0xFFBBDEFB);
    val accent2 = Color(0xFF64B5F6)
    val accent3 = Color(0xFF1E88E5)
    val accent4 = Color(0xFF1E88E5)
    val primary1 = Color(0xFF003297)
    val primary2 = Color(0xFF00287A)
    val primary3 = Color(0xFF001F5C)
    val primaryDark1 = Color(0xFF002548)
    val primaryDark2 = Color(0xFF001A33)
    val primaryDark3 = Color(0xFF000F40)

    // Optional helper for light/dark background color
    fun bgColorWB(isLight: Boolean): Color {
        return if (isLight) Color.White else primaryDark1
    }

    // Optional Compose helper if you want to pass Compose theme directly
//    @Composable
//    fun bgColorWB(): Color {
//        return if (MaterialTheme.colorScheme.isLight) white else primaryDark
//    }
}
