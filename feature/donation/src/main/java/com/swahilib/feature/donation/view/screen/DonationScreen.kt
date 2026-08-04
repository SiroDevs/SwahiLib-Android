package com.swahilib.feature.donation.view.screen

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.ui.components.action.AppTopBar
import com.swahilib.feature.donation.viewmodel.DonationState
import com.swahilib.feature.donation.viewmodel.DonationViewModel
import com.swahilib.feature.donation.view.components.ConfirmDonationDialog
import com.swahilib.feature.donation.view.components.DonateNowButton
import com.swahilib.feature.donation.view.components.DonationHeaderCard
import com.swahilib.feature.donation.view.components.DonorIdentitySection
import com.swahilib.feature.donation.view.components.PresetAmountGrid
import kotlinx.coroutines.launch

private const val DEFAULT_PRESET = 500
private const val MINIMUM_DONATION = 50

@Composable
fun DonationScreen(
    navController: NavHostController,
    viewModel: DonationViewModel,
) {
    val state by viewModel.state.collectAsState()

    var selectedPreset by remember { mutableStateOf<Int?>(DEFAULT_PRESET) }
    var customAmount by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showMinimumAmountError by remember { mutableStateOf(false) }

    var donorName by remember { mutableStateOf("") }
    var donorEmail by remember { mutableStateOf("") }
    var isDonatingAnonymously by remember { mutableStateOf(false) }
    var isDonorEmailError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val activeAmount: Double? = when {
        customAmount.isNotBlank() -> {
            val amount = customAmount.toDoubleOrNull()
            if (amount != null && amount >= MINIMUM_DONATION) amount else null
        }
        selectedPreset != null -> selectedPreset!!.toDouble()
        else -> null
    }

    val isCustomAmountBelowMinimum = customAmount.isNotBlank() &&
        (customAmount.toDoubleOrNull() ?: 0.0) < MINIMUM_DONATION

    LaunchedEffect(state) {
        when (state) {
            is DonationState.ReadyToPay -> {
                val redirectUrl = (state as DonationState.ReadyToPay).redirectUrl
                navController.navigate(Routes.paymentWebView(redirectUrl))
            }
            is DonationState.Error -> {
                val msg = (state as DonationState.Error).message
                scope.launch { snackbarHostState.showSnackbar(msg) }
                viewModel.resetState()
            }
            else -> {}
        }
    }

    if (showConfirmDialog && activeAmount != null) {
        ConfirmDonationDialog(
            amount = activeAmount,
            donorName = donorName.trim().takeIf { !isDonatingAnonymously && it.isNotBlank() },
            onConfirm = {
                showConfirmDialog = false
                viewModel.submitDonation(
                    amountUsd = activeAmount,
                    donorName = if (isDonatingAnonymously) null else donorName.trim()
                        .takeIf { it.isNotBlank() },
                    donorEmail = if (isDonatingAnonymously) null else donorEmail.trim()
                        .takeIf { it.isNotBlank() },
                )
            },
            onDismiss = {
                showConfirmDialog = false
                showMinimumAmountError = false
            },
        )
    }

    LaunchedEffect(showMinimumAmountError) {
        if (showMinimumAmountError) {
            scope.launch {
                snackbarHostState.showSnackbar("Minimum donation amount is KES $MINIMUM_DONATION")
            }
            showMinimumAmountError = false
        }
    }

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

                Text(
                    text = "Donation amount (KES)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                PresetAmountGrid(
                    selectedPreset = selectedPreset,
                    onPresetSelected = { amount ->
                        selectedPreset = amount
                        customAmount = ""
                        showMinimumAmountError = false
                    },
                )

                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' }
                        val dotCount = filtered.count { it == '.' }
                        if (dotCount <= 1) {
                            customAmount = filtered
                            if (filtered.isNotBlank()) {
                                selectedPreset = null
                                showMinimumAmountError = false
                            }
                        }
                    },
                    label = { Text("Or input your amount (KES)") },
                    placeholder = { Text("Minimum is 50") },
                    prefix = { Text("KES") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isCustomAmountBelowMinimum,
                    supportingText = {
                        if (isCustomAmountBelowMinimum) {
                            Text(
                                text = "Minimum amount is KES $MINIMUM_DONATION",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                DonorIdentitySection(
                    name = donorName,
                    onNameChange = { donorName = it },
                    email = donorEmail,
                    onEmailChange = {
                        donorEmail = it
                        isDonorEmailError = false
                    },
                    isAnonymous = isDonatingAnonymously,
                    onAnonymousToggle = { isDonatingAnonymously = it },
                    isEmailError = isDonorEmailError,
                )

                Spacer(Modifier.height(4.dp))

                DonateNowButton(
                    isLoading = state is DonationState.Loading,
                    enabled = state !is DonationState.Loading && activeAmount != null && activeAmount >= MINIMUM_DONATION,
                    onClick = {
                        when {
                            activeAmount == null -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please enter a donation amount")
                                }
                            }

                            activeAmount < MINIMUM_DONATION -> {
                                showMinimumAmountError = true
                            }

                            !isDonatingAnonymously && donorEmail.isNotBlank() && !isValidEmail(
                                donorEmail
                            ) -> {
                                isDonorEmailError = true
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please enter a valid email address")
                                }
                            }

                            else -> {
                                showConfirmDialog = true
                            }
                        }
                    },
                )

                Text(
                    text = "Donations are processed securely via Paystack",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

private fun isValidEmail(email: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(email).matches()