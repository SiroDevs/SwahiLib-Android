package com.swahilib.presentation.theme

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.*
import androidx.core.view.WindowCompat

private val LightTheme = lightColorScheme(
    primary = LightColors.primary, // Main brand color
    onPrimary = LightColors.onPrimary, // Text/icon color on primary
    primaryContainer = LightColors.primaryContainer, // Primary color for large surfaces (e.g., cards, sheets)
    onPrimaryContainer = LightColors.onPrimaryContainer, // Text/icon color on primary container

    secondary = LightColors.secondary, // Secondary brand color (less emphasis than primary)
    onSecondary = LightColors.onSecondary, // Text/icon color on secondary
    secondaryContainer = LightColors.secondaryContainer, // Background using secondary color
    onSecondaryContainer = LightColors.onSecondaryContainer, // Text/icon on secondary container

    tertiary = LightColors.tertiary, // Optional 3rd brand color (e.g., for accents)
    onTertiary = LightColors.onTertiary, // Text/icon on tertiary
    tertiaryContainer = LightColors.tertiaryContainer, // Background using tertiary
    onTertiaryContainer = LightColors.onTertiaryContainer, // Text/icon on tertiary container

    error = LightColors.error, // Color for errors (e.g., validation)
    errorContainer = LightColors.errorContainer, // Background for error messages
    onError = LightColors.onError, // Text/icon on error color
    onErrorContainer = LightColors.onErrorContainer, // Text/icon on error container

    background = LightColors.background, // App background
    onBackground = LightColors.onBackground, // Text/icon color on background

    surface = LightColors.surface, // Surface color for UI components like cards
    onSurface = LightColors.onSurface, // Text/icon color on surface

    surfaceVariant = LightColors.surfaceVariant, // Variant surface for emphasis layering
    onSurfaceVariant = LightColors.onSurfaceVariant, // Text/icon on surface variant

    outline = LightColors.outline, // Used for thin dividers/borders
    inverseOnSurface = LightColors.inverseOnSurface, // Text/icon on dark surface (e.g., bottom sheet in light mode)
    inverseSurface = LightColors.inverseSurface, // Used for elevated surfaces in dark over light
    inversePrimary = LightColors.inversePrimary, // Primary color when used over dark surfaces

    surfaceTint = LightColors.surfaceTint, // Tint used in elevation overlay
    outlineVariant = LightColors.outlineVariant, // Faint outlines, used for background dividers
    scrim = LightColors.scrim, // Used for overlays like modals and dialogs
)

private val DarkTheme = darkColorScheme(
    primary = DarkColors.primary, // Main brand color
    onPrimary = DarkColors.onPrimary, // Text/icon color on primary
    primaryContainer = DarkColors.primaryContainer, // Primary color for large surfaces
    onPrimaryContainer = DarkColors.onPrimaryContainer, // Text/icon color on primary container

    secondary = DarkColors.secondary, // Secondary brand color
    onSecondary = DarkColors.onSecondary, // Text/icon color on secondary
    secondaryContainer = DarkColors.secondaryContainer, // Background using secondary color
    onSecondaryContainer = DarkColors.onSecondaryContainer, // Text/icon on secondary container

    tertiary = DarkColors.tertiary, // Optional accent color
    onTertiary = DarkColors.onTertiary, // Text/icon on tertiary
    tertiaryContainer = DarkColors.tertiaryContainer, // Background using tertiary
    onTertiaryContainer = DarkColors.onTertiaryContainer, // Text/icon on tertiary container

    error = DarkColors.error, // Color for errors
    errorContainer = DarkColors.errorContainer, // Background for error messages
    onError = DarkColors.onError, // Text/icon on error color
    onErrorContainer = DarkColors.onErrorContainer, // Text/icon on error container

    background = DarkColors.background, // App background
    onBackground = DarkColors.onBackground, // Text/icon color on background

    surface = DarkColors.surface, // Surface color for UI components
    onSurface = DarkColors.onSurface, // Text/icon color on surface

    surfaceVariant = DarkColors.surfaceVariant, // Variant surface for emphasis layering
    onSurfaceVariant = DarkColors.onSurfaceVariant, // Text/icon on surface variant

    outline = DarkColors.outline, // Used for thin dividers/borders
    inverseOnSurface = DarkColors.inverseOnSurface, // Text/icon on light surface (e.g., bottom sheet in dark mode)
    inverseSurface = DarkColors.inverseSurface, // Used for elevated surfaces in light over dark
    inversePrimary = DarkColors.inversePrimary, // Primary color when used over light surfaces

    surfaceTint = DarkColors.surfaceTint, // Tint used in elevation overlay
    outlineVariant = DarkColors.outlineVariant, // Faint outlines, used for background dividers
    scrim = DarkColors.scrim, // Used for overlays like modals and dialogs
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        useDarkTheme -> DarkTheme
        else -> LightTheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                    val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                    view.setBackgroundColor(colorScheme.onPrimary.toArgb())

                    view.setPadding(0, statusBarInsets.top, 0, 0)
                    insets
                }
            } else {
                @Suppress("DEPRECATION")
                window.statusBarColor = colorScheme.onPrimary.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
        shapes = shapes,
    )
}