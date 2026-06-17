package com.swahilib.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.indicators.ShimmerBrush

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeSkeleton() {
    var selectedType by rememberSaveable { mutableStateOf("MANENO") }
    val types = listOf("MANENO", "NAHAU", "METHALI", "MISEMO")

    Column(modifier = Modifier.fillMaxSize()) {
        SearchFieldSkeleton()

        LazyRow(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(types) { type ->
                SkeletonFilterChip(
                    text = type,
                    selected = selectedType == type,
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            HomeSearchResultsSkeleton()
            VerticalLettersSkeleton()
        }
    }
}

@Composable
fun SkeletonFilterChip(
    text: String,
    selected: Boolean
) {
    val brush = ShimmerBrush()
    Surface(
        modifier = Modifier.height(32.dp).width(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        border = if (selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.Transparent,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}