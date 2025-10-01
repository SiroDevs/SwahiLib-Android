package com.swahilib.presentation.screens.splash

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.swahilib.R
import com.swahilib.core.utils.AppConstants

@Preview(showBackground = true)
@Composable
fun SplashContent() {
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.onPrimary)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = AppConstants.APP_TITLE,
                        style = TextStyle(
                            fontSize = 50.sp,
                            letterSpacing = 5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = AppConstants.APP_TITLE2,
                        style = TextStyle(
                            fontSize = 35.sp,
                            letterSpacing = 5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    Divider(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        thickness = 2.dp,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(1.dp)
                    )
                    KiswahiliKitukuzwe()
                    AppCredits()
                    Spacer(Modifier.height(50.dp))
                }
            }
        },
    )
}

@Composable
fun KiswahiliKitukuzwe() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = AppConstants.APP_TAGLINE,
            style = TextStyle(
                fontSize = 22.sp,
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
                fontSize = 18.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}