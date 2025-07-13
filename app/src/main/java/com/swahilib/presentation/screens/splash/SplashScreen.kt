package com.swahilib.presentation.screens.splash

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.swahilib.R
import com.swahilib.core.utils.Preferences
import com.swahilib.presentation.navigation.Routes
import com.swahilib.presentation.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000)

        val prefs =
            context.getSharedPreferences(Preferences.PREFERENCE_FILE, Context.MODE_PRIVATE)
        val isDataLoaded = prefs.getBoolean(Preferences.IS_DATA_LOADED, false)

        val nextRoute = when {
            isDataLoaded -> Routes.HOME
            else -> Routes.INIT
        }

        navController.navigate(nextRoute) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    SplashContent()
}

@Preview(showBackground = true)
@Composable
fun SplashContent() {
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    //.background(color = ThemeColors.accent2)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "App logo",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "SwahiLib",
                        style = TextStyle(
                            fontSize = 50.sp,
                            letterSpacing = 5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ThemeColors.primary1
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    Divider(
                        color = ThemeColors.primaryDark1,
                        thickness = 2.dp,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(1.dp)
                    )
                    KiswahiliKitukuzwe()
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
            text = "Kiswahili Kitukuzwe",
            style = TextStyle(
                fontSize = 25.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.primary1
            )
        )
    }
}