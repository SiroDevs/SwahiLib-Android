package com.swahilib.navigation.graphs

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.PrefsRepo
import com.swahilib.feature.donation.view.screen.DonationScreen
import com.swahilib.feature.donation.view.screen.PaymentWebViewScreen
import com.swahilib.feature.donation.viewmodel.DonationViewModel
import kotlinx.coroutines.launch

/** Donation flow: the donation screen and its payment webview redirect. */
fun NavGraphBuilder.donationGraph(
    navController: NavHostController,
    prefsRepo: PrefsRepo,
) {
    composable(Routes.DONATION) {
        val viewModel: DonationViewModel = hiltViewModel()
        DonationScreen(navController = navController, viewModel = viewModel)
    }

    composable(
        route = Routes.PAYMENT_WEBVIEW,
        arguments = listOf(
            navArgument("redirectUrl") { type = NavType.StringType }
        ),
    ) { backStackEntry ->
        val encoded = backStackEntry.arguments?.getString("redirectUrl") ?: ""
        val redirectUrl = Routes.decodeRedirectUrl(encoded)

        val donationEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Routes.DONATION)
        }
        val viewModel: DonationViewModel = hiltViewModel(donationEntry)
        val scope = rememberCoroutineScope()

        PaymentWebViewScreen(
            navController = navController,
            viewModel = viewModel,
            redirectUrl = redirectUrl,
            onPaymentComplete = { isSuccess ->
                if (isSuccess) {
                    scope.launch { prefsRepo.recordDonation() }
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                } else {
                    viewModel.resetState()
                    navController.popBackStack()
                }
            },
        )
    }
}
