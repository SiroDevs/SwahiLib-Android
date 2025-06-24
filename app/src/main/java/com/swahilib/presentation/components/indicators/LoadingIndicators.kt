package com.swahilib.presentation.components.indicators

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.*
import com.swahilib.presentation.theme.ThemeColors

@Composable
fun LoadingState(
    title: String,
    fileName: String,
    showProgress: Boolean = false,
    progressValue: Int = 0,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("$fileName.json")
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(20.dp)
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primaryDark1
            )

            if (showProgress) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { (progressValue / 100f).coerceIn(0f, 1f) },
                        color = ThemeColors.primary1,
                        trackColor = Color.White,
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$progressValue %",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThemeColors.primaryDark3,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingStatePreview() {
    LoadingState(
        title = "Loading",
        fileName = "opener-loading",
    )
}
