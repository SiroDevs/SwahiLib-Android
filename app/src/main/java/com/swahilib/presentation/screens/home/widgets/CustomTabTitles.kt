package com.swahilib.presentation.screens.home.widgets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swahilib.domain.entity.HomeTab
import com.swahilib.domain.entity.homeTabs
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun CustomTabTitles(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Surface(
        color = Color.LightGray,
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(bottomEnd = 15.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))

                homeTabs.forEach { tab ->
                    TabItem(
                        text = tab.title.uppercase(),
                        isSelected = selectedTab == tab,
                        onClick = { onTabSelected(tab) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) ThemeColors.primary1 else Color.Transparent
    val contentColor = if (isSelected) Color.White else ThemeColors.primary1

    Surface(
        modifier = Modifier
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Tab
            ),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (isSelected) 0.dp else 1.dp,
            color = ThemeColors.primary1
        )
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTabTitlesPreview() {
    CustomTabTitles(
        selectedTab = HomeTab.Words,
        onTabSelected = { }
    )
}
