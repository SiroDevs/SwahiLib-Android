package com.swahilib.feature.idiom.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swahilib.core.ui.components.donation.DonationBanner
import com.swahilib.core.ui.components.general.CollapsingHeader
import com.swahilib.core.ui.components.general.MeaningsView

@Composable
fun IdiomView(
    modifier: Modifier = Modifier,
    title: String,
    meanings: List<String>,
    showDonation: Boolean = false,
    onShowDonation: () -> Unit = {},
) {
    val scrollState = rememberLazyListState()
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(state = scrollState) {
            item { CollapsingHeader(title = title) }
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (meanings.isNotEmpty()) MeaningsView(meanings = meanings)
                }
            }
            item {
                DonationBanner(
                    show = showDonation,
                    onTap = onShowDonation,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }
        }
    }
}
