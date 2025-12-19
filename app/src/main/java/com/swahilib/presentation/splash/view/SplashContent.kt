package com.swahilib.presentation.splash.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.swahilib.core.utils.AppConstants

@Composable
fun KiswahiliKitukuzwe() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = AppConstants.APP_TAGLINE,
            style = TextStyle(
                fontSize = 16.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun AppCredits() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = AppConstants.APP_CREDITS,
            style = TextStyle(
                fontSize = 12.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}