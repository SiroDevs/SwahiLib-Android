package com.swahilib.feature.advsearch.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.feature.advsearch.viewmodel.SortOrder

@Composable
fun SortDropdown(
    current: SortOrder,
    onSelect: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (current) {
        SortOrder.AZ          -> "A→Z"
        SortOrder.ZA          -> "Z→A"
        SortOrder.LIKED_FIRST -> "♥"
    }
    Box(modifier = modifier) {
        TextButton(onClick = { expanded = true }) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("A → Z") },
                leadingIcon = { Icon(Icons.Filled.SortByAlpha, null, Modifier.size(18.dp)) },
                trailingIcon = { if (current == SortOrder.AZ) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) },
                onClick = { onSelect(SortOrder.AZ); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Z → A") },
                leadingIcon = { Icon(Icons.Filled.SortByAlpha, null, Modifier.size(18.dp)) },
                trailingIcon = { if (current == SortOrder.ZA) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) },
                onClick = { onSelect(SortOrder.ZA); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Vipendwa kwanza") },
                leadingIcon = { Icon(Icons.Filled.FavoriteBorder, null, Modifier.size(18.dp)) },
                trailingIcon = { if (current == SortOrder.LIKED_FIRST) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) },
                onClick = { onSelect(SortOrder.LIKED_FIRST); expanded = false }
            )
        }
    }
}

@Composable
fun TypeFilterRow(
    types: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(types) { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(type) },
                leadingIcon = if (selected == type) {
                    { Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }
                } else null
            )
        }
    }
}