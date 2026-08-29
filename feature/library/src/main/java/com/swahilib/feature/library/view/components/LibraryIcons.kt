package com.swahilib.feature.library.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.filled.Water
import androidx.compose.ui.graphics.vector.ImageVector

/** Resolves the plain icon name stored in [com.swahilib.core.common.library.LibraryCollectionConfig]
 *  to an actual [ImageVector], keeping `core:common` free of a Compose dependency. */
fun resolveLibraryIcon(name: String): ImageVector = when (name) {
    "Groups" -> Icons.Default.Groups
    "Checkroom" -> Icons.Default.Checkroom
    "SetMeal" -> Icons.Default.SetMeal
    "BugReport" -> Icons.Default.BugReport
    "Water" -> Icons.Default.Water
    "Casino" -> Icons.Default.Casino
    "WavingHand" -> Icons.Default.WavingHand
    "FormatQuote" -> Icons.Default.FormatQuote
    "Public" -> Icons.Default.Public
    else -> Icons.Default.MenuBook
}
