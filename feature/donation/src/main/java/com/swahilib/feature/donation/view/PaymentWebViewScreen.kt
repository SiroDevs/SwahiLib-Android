package com.swahilib.feature.donation.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.ApiConstants
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.donation.DonationViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentWebViewScreen(
    navController: NavHostController,
    viewModel: DonationViewModel,
    redirectUrl: String,
    onPaymentComplete: (success: Boolean) -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            Column {
                AppTopBar(
                    title = "Kamilisha Mchango Wako",
                    showGoBack = true,
                    onNavIconClick = {
                        viewModel.resetState()
                        navController.popBackStack()
                    },
                )
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LinearProgressIndicator(
                        progress = { loadProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        @Suppress("DEPRECATION")
                        settings.setSupportZoom(false)

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadProgress = newProgress / 100f
                                if (newProgress == 100) isLoading = false
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?,
                            ) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?,
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false

                                if (url.startsWith(ApiConstants.PAYSTACK_CALLBACK_URL)) {
                                    val isCancelled = url.contains("cancelled=true", ignoreCase = true)
                                    val hasReference = url.contains("reference=", ignoreCase = true)
                                    onPaymentComplete(!isCancelled && hasReference)
                                    return true
                                }

                                return false
                            }
                        }

                        loadUrl(redirectUrl)
                    }
                },
            )
        }
    }
}
