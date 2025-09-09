package com.swahilib.presentation.screens.viewer.idiom

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.swahilib.presentation.components.general.*

@Composable
fun IdiomView(
    modifier: Modifier = Modifier,
    title: String,
    meanings: List<String>,
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        LazyColumn(state = scrollState) {
            item {
                CollapsingHeader(title = title)
            }
            item {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (meanings.isNotEmpty()) {
                        MeaningsView(meanings = meanings)
                    }
                }
            }
        }
    }
}
