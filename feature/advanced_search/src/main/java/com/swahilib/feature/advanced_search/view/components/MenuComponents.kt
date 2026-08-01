package com.swahilib.feature.advanced_search.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.feature.advanced_search.viewmodel.SearchMode

@Composable
fun SearchModeMenu(
    current: SearchMode,
    onSelect: (SearchMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Tune, contentDescription = "Chaguo za utafutaji")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Tafuta Mwanzo wa Maneno") },
                trailingIcon = { if (current == SearchMode.BEGINNING) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) },
                onClick = { onSelect(SearchMode.BEGINNING); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Tafuta Katikati ya Maneno") },
                trailingIcon = { if (current == SearchMode.MIDDLE) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) },
                onClick = { onSelect(SearchMode.MIDDLE); expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Tafuta Mwisho wa Maneno") },
                trailingIcon = { if (current == SearchMode.END) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) },
                onClick = { onSelect(SearchMode.END); expanded = false }
            )
        }
    }
}
