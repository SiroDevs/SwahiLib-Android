package com.swahilib.feature.donation.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.swahilib.core.common.entity.DonationMethod
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.donation.viewmodel.DonationViewModel
import com.swahilib.feature.donation.view.components.CryptoDonationSection
import com.swahilib.feature.donation.view.components.DiyDonationSection
import com.swahilib.feature.donation.view.components.DonationHeaderCard
import com.swahilib.feature.donation.view.components.DonationMethodFilterStrip
import com.swahilib.feature.donation.view.components.PaystackDonationSection
import kotlinx.coroutines.launch

@Composable
fun DonationScreen(
    navController: NavHostController,
    viewModel: DonationViewModel,
) {
    var selectedMethod by remember { mutableStateOf(DonationMethod.DIY) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            AppTopBar(
                title = "Donate to SwahiLib",
                showGoBack = true,
                onNavIconClick = { navController.popBackStack() },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                DonationHeaderCard()

                DonationMethodFilterStrip(
                    selectedMethod = selectedMethod,
                    onMethodSelected = { selectedMethod = it },
                )

                when (selectedMethod) {
                    DonationMethod.DIY -> {
                        DiyDonationSection(
                            onItemCopied = { item ->
                                viewModel.recordCryptoDonation()
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "$item copied — thank you for supporting SwahiLib!"
                                    )
                                }
                            },
                        )
                    }
                    DonationMethod.PAYSTACK -> {
                        PaystackDonationSection(
                            navController = navController,
                            viewModel = viewModel,
                            sbHostState = snackbarHostState,
                            scope = scope,
                        )
                    }
                    else -> {
                        CryptoDonationSection(
                            onAddressCopied = { network ->
                                viewModel.recordCryptoDonation()
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "$network address copied — thank you for supporting SwahiLib!"
                                    )
                                }
                            },
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
