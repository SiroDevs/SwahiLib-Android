package com.swahilib.core.design_system.theme

import androidx.compose.ui.graphics.Color

object LightColors {
    val primary = Color(0xFF1565C0)           // Deep blue - brand
    val onPrimary = Color(0xFFFFFFFF)         // White text on primary
    val primaryContainer = Color(0xFFD6E4FF)  // Soft blue - cards/containers
    val onPrimaryContainer = Color(0xFF001A57) // Dark text on primary container

    val secondary = Color(0xFF0288D1)          // Sky blue - accent
    val onSecondary = Color(0xFFFFFFFF)
    val secondaryContainer = Color(0xFFCCE9FF)
    val onSecondaryContainer = Color(0xFF001B3D)

    val tertiary = Color(0xFF00838F)           // Teal - 3rd accent
    val onTertiary = Color(0xFFFFFFFF)
    val tertiaryContainer = Color(0xFFB2EBF2)
    val onTertiaryContainer = Color(0xFF002A2E)

    val error = Color(0xFFBA1A1A)
    val errorContainer = Color(0xFFFFDAD6)
    val onError = Color(0xFFFFFFFF)
    val onErrorContainer = Color(0xFF410002)

    val background = Color(0xFFF5F8FF)        // Very light blue-white
    val onBackground = Color(0xFF1A1C1E)

    val surface = Color(0xFFFFFFFF)           // Pure white for cards
    val onSurface = Color(0xFF1A1C1E)

    val surfaceVariant = Color(0xFFE3EEFF)    // Sticky headers background
    val onSurfaceVariant = Color(0xFF44474F)

    val outline = Color(0xFF74777F)
    val outlineVariant = Color(0xFFC3C7CF)

    val inverseOnSurface = Color(0xFFF0F0F7)
    val inverseSurface = Color(0xFF2E3035)
    val inversePrimary = Color(0xFFADC6FF)

    val shadow = Color.Black
    val surfaceTint = Color(0xFF1565C0)
    val scrim = Color.Black
}

object DarkColors {
    val primary = Color(0xFFADC6FF)           // Soft blue for dark mode
    val onPrimary = Color(0xFF002E6C)
    val primaryContainer = Color(0xFF0D47A1)  // Darker container
    val onPrimaryContainer = Color(0xFFD6E4FF)

    val secondary = Color(0xFF81D4FA)
    val onSecondary = Color(0xFF003549)
    val secondaryContainer = Color(0xFF004D67)
    val onSecondaryContainer = Color(0xFFCCE9FF)

    val tertiary = Color(0xFF4DD0E1)
    val onTertiary = Color(0xFF003740)
    val tertiaryContainer = Color(0xFF004F5A)
    val onTertiaryContainer = Color(0xFFB2EBF2)

    val error = Color(0xFFFFB4AB)
    val errorContainer = Color(0xFF93000A)
    val onError = Color(0xFF690005)
    val onErrorContainer = Color(0xFFFFDAD6)

    val background = Color(0xFF0D1117)        // Very dark blue-black
    val onBackground = Color(0xFFE2E2E9)

    val surface = Color(0xFF1A1F2C)           // Dark card surface
    val onSurface = Color(0xFFE2E2E9)

    val surfaceVariant = Color(0xFF1E2A3A)
    val onSurfaceVariant = Color(0xFFC3C7CF)

    val outline = Color(0xFF8D9199)
    val outlineVariant = Color(0xFF2D3748)

    val inverseOnSurface = Color(0xFF1A1C1E)
    val inverseSurface = Color(0xFFE2E2E9)
    val inversePrimary = Color(0xFF1565C0)

    val shadow = Color.Black
    val surfaceTint = Color(0xFFADC6FF)
    val scrim = Color.Black
}
