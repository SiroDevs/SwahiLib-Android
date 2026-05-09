package com.swahilib.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swahilib.core.common.entity.HomeTab
import com.swahilib.core.common.entity.homeTabs

@Composable
fun CustomTabTitles(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Surface(
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

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary

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
            color = MaterialTheme.colorScheme.primary
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
