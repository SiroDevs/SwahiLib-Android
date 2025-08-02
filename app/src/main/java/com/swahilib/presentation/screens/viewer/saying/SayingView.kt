package com.swahilib.presentation.screens.viewer.saying

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.swahilib.presentation.components.general.CollapsingHeader
import com.swahilib.presentation.components.general.MeaningsView
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun SayingView(
    modifier: Modifier = Modifier,
    title: String,
    meanings: List<String>,
) {
    val scrollState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeColors.accent0)
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
