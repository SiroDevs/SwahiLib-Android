package com.swahilib.feature.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.indicators.ShimmerBrush
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeSkeleton() {
    val brush = ShimmerBrush()
    var selectedType by rememberSaveable { mutableStateOf("MANENO") }
    val types = listOf("MANENO", "NAHAU", "METHALI", "MISEMO")
    Column(modifier = Modifier.fillMaxSize()) {
        SearchFieldSkeleton()

        LazyRow(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(types) { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { },
                    label = { Text(type, color = Color.Transparent) },
                    modifier = Modifier.background(brush)
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {

            HomeSearchResultsSkeleton()

            VerticalLettersSkeleton()
        }
    }
}
