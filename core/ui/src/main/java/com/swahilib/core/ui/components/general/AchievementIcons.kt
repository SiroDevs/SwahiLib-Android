package com.swahilib.core.ui.components.general

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

/** Maps `Achievement.iconKey` (AchievementCatalog) to an actual Material icon. */
fun achievementIconFor(iconKey: String): ImageVector = when (iconKey) {
    "footprints" -> Icons.Filled.DirectionsWalk
    "flame" -> Icons.Filled.LocalFireDepartment
    "trophy" -> Icons.Filled.EmojiEvents
    "crown" -> Icons.Filled.WorkspacePremium
    "star" -> Icons.Filled.Star
    "book" -> Icons.Filled.MenuBook
    "target" -> Icons.Filled.GpsFixed
    "medal" -> Icons.Filled.MilitaryTech
    "puzzle" -> Icons.Filled.Extension
    "grid" -> Icons.Filled.GridOn
    "pencil" -> Icons.Filled.Edit
    "scroll" -> Icons.Filled.Article
    else -> Icons.Filled.Star
}
