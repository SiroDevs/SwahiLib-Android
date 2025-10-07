package com.swahilib.presentation.components.action

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.revenuecat.purchases.ui.revenuecatui.*
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallSheet(
    isProUser: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val paywallOptions = remember {
        PaywallOptions.Builder(dismissRequest = onDismissRequest)
            .setShouldDisplayDismissButton(true)
            .build()
    }

    Box(modifier = modifier) {
        if (!isProUser) {
            Paywall(paywallOptions)
        } else {
            CustomerCenter(onDismiss = onDismissRequest)
        }
    }
}