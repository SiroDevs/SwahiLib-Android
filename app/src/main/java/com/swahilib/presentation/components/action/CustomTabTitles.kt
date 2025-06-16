package com.swahilib.presentation.components.action

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun CustomTabTitles() {
    val tabs = listOf("MANENO", "NAHAU", "MISEMO", "METHALI")
    var selectedTab by remember { mutableStateOf(tabs[0]) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEach { tab ->
            TabItem(
                text = tab,
                isSelected = selectedTab == tab,
                onClick = { selectedTab = tab }
            )
        }
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) ThemeColors.primary else ThemeColors.accent
    val contentColor = if (isSelected) Color.White else ThemeColors.primary

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Tab
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTabTitlesPreview() {
    CustomTabTitles()
}
