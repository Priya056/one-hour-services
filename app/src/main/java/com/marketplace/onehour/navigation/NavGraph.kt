package com.marketplace.onehour.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.marketplace.onehour.customer.presentation.auth.LoginRegisterScreen
import com.marketplace.onehour.customer.presentation.booking.BookingScreen
import com.marketplace.onehour.customer.presentation.chat.ChatScreen
import com.marketplace.onehour.customer.presentation.confirmation.BookingConfirmationScreen
import com.marketplace.onehour.customer.presentation.filter.FiltersSheet
import com.marketplace.onehour.customer.presentation.history.BookingHistoryScreen
import com.marketplace.onehour.customer.presentation.home.HomeScreen
import com.marketplace.onehour.customer.presentation.payment.PaymentScreen
import com.marketplace.onehour.customer.presentation.profile.HelperProfileScreen
import com.marketplace.onehour.customer.presentation.review.RateReviewScreen
import com.marketplace.onehour.customer.presentation.settings.CustomerSettingsScreen
import com.marketplace.onehour.customer.presentation.splash.SplashScreen
import com.marketplace.onehour.customer.presentation.tracking.LiveTrackingScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.CustomerSplash.route
    ) {
        // Customer Routes
        composable(ScreenRoutes.CustomerSplash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(ScreenRoutes.CustomerAuth.route) {
                        popUpTo(ScreenRoutes.CustomerSplash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.CustomerAuth.route) {
            LoginRegisterScreen(
                onLoginSuccess = {
                    navController.navigate(ScreenRoutes.CustomerHome.route) {
                        popUpTo(ScreenRoutes.CustomerAuth.route) { inclusive = true }
                    }
                },
                onSwitchToHelperOnboarding = {
                    navController.navigate(ScreenRoutes.HelperOnboarding.route)
                }
            )
        }

        composable(ScreenRoutes.CustomerHome.route) {
            HomeScreen(
                onHelperClick = { helperId ->
                    navController.navigate(ScreenRoutes.HelperProfile.createRoute(helperId))
                },
                onOpenFilterSheet = {
                    navController.navigate(ScreenRoutes.CustomerFilter.route)
                },
                onNavigateBottom = { route ->
                    if (route != ScreenRoutes.CustomerHome.route) {
                        navController.navigate(route)
                    }
                }
            )
        }

        composable(ScreenRoutes.CustomerFilter.route) {
            FiltersSheet(
                onDismiss = { navController.popBackStack() },
                onApply = { navController.popBackStack() }
            )
        }

        composable(ScreenRoutes.HelperProfile.route) { backStack ->
            val helperId = backStack.arguments?.getString("helperId") ?: "h1"
            HelperProfileScreen(
                helperId = helperId,
                onBackClick = { navController.popBackStack() },
                onBookNowClick = { id ->
                    navController.navigate(ScreenRoutes.BookingFlow.createRoute(id))
                }
            )
        }

        composable(ScreenRoutes.BookingFlow.route) { backStack ->
            val helperId = backStack.arguments?.getString("helperId") ?: "h1"
            BookingScreen(
                helperId = helperId,
                onBackClick = { navController.popBackStack() },
                onProceedToPayment = { bookingId ->
                    navController.navigate(ScreenRoutes.Payment.createRoute(bookingId))
                }
            )
        }

        composable(ScreenRoutes.Payment.route) { backStack ->
            val bookingId = backStack.arguments?.getString("bookingId") ?: "b101"
            PaymentScreen(
                bookingId = bookingId,
                onBackClick = { navController.popBackStack() },
                onPaymentSuccess = { id ->
                    navController.navigate(ScreenRoutes.BookingConfirmation.createRoute(id)) {
                        popUpTo(ScreenRoutes.CustomerHome.route)
                    }
                }
            )
        }

        composable(ScreenRoutes.BookingConfirmation.route) { backStack ->
            val bookingId = backStack.arguments?.getString("bookingId") ?: "b101"
            BookingConfirmationScreen(
                bookingId = bookingId,
                onTrackLiveBooking = { id ->
                    navController.navigate(ScreenRoutes.LiveTracking.createRoute(id))
                },
                onBackToHome = {
                    navController.navigate(ScreenRoutes.CustomerHome.route) {
                        popUpTo(ScreenRoutes.CustomerHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.LiveTracking.route) { backStack ->
            val bookingId = backStack.arguments?.getString("bookingId") ?: "b101"
            LiveTrackingScreen(
                bookingId = bookingId,
                onBackClick = { navController.popBackStack() },
                onOpenChat = { bId, hId ->
                    navController.navigate(ScreenRoutes.Chat.createRoute(bId, hId))
                },
                onRateReview = { bId ->
                    navController.navigate(ScreenRoutes.RateReview.createRoute(bId))
                }
            )
        }

        composable(ScreenRoutes.Chat.route) { backStack ->
            val bookingId = backStack.arguments?.getString("bookingId") ?: "b101"
            val helperId = backStack.arguments?.getString("helperId") ?: "h1"
            ChatScreen(
                bookingId = bookingId,
                helperId = helperId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(ScreenRoutes.BookingHistory.route) {
            BookingHistoryScreen(
                onTrackLive = { bId -> navController.navigate(ScreenRoutes.LiveTracking.createRoute(bId)) },
                onOpenChat = { bId, hId -> navController.navigate(ScreenRoutes.Chat.createRoute(bId, hId)) },
                onRateReview = { bId -> navController.navigate(ScreenRoutes.RateReview.createRoute(bId)) },
                onRebookHelper = { hId -> navController.navigate(ScreenRoutes.HelperProfile.createRoute(hId)) },
                onNavigateBottom = { route ->
                    if (route != ScreenRoutes.BookingHistory.route) {
                        navController.navigate(route)
                    }
                }
            )
        }

        composable(ScreenRoutes.RateReview.route) { backStack ->
            val bookingId = backStack.arguments?.getString("bookingId") ?: "b101"
            RateReviewScreen(
                bookingId = bookingId,
                onBackClick = { navController.popBackStack() },
                onReviewSubmitted = {
                    navController.navigate(ScreenRoutes.BookingHistory.route) {
                        popUpTo(ScreenRoutes.CustomerHome.route)
                    }
                }
            )
        }

        composable(ScreenRoutes.CustomerSettings.route) {
            CustomerSettingsScreen(
                onSwitchToHelperMode = {
                    navController.navigate(ScreenRoutes.HelperOnboarding.route)
                },
                onLogout = {
                    navController.navigate(ScreenRoutes.CustomerAuth.route) {
                        popUpTo(ScreenRoutes.CustomerHome.route) { inclusive = true }
                    }
                },
                onNavigateBottom = { route ->
                    if (route != ScreenRoutes.CustomerSettings.route) {
                        navController.navigate(route)
                    }
                }
            )
        }

        // Helper Routes
        composable(ScreenRoutes.HelperOnboarding.route) {
            PlaceholderScreen("Helper Onboarding")
        }

        composable(ScreenRoutes.HelperProfileCreation.route) {
            PlaceholderScreen("Helper Profile Creation")
        }

        composable(ScreenRoutes.KycUpload.route) {
            PlaceholderScreen("KYC Upload")
        }

        composable(ScreenRoutes.HourlyRateSchedule.route) {
            PlaceholderScreen("Hourly Rate & Schedule")
        }

        composable(ScreenRoutes.HelperHome.route) {
            PlaceholderScreen("Helper Home")
        }

        composable(ScreenRoutes.IncomingRequests.route) {
            PlaceholderScreen("Incoming Requests")
        }

        composable(ScreenRoutes.ActiveBooking.route) { backStack ->
            val bookingId = backStack.arguments?.getString("bookingId") ?: ""
            PlaceholderScreen("Active Booking ($bookingId)")
        }

        composable(ScreenRoutes.EarningsDashboard.route) {
            PlaceholderScreen("Earnings Dashboard")
        }

        composable(ScreenRoutes.Wallet.route) {
            PlaceholderScreen("Wallet")
        }

        composable(ScreenRoutes.TransactionHistory.route) {
            PlaceholderScreen("Transaction History")
        }

        composable(ScreenRoutes.HelperReviews.route) {
            PlaceholderScreen("Helper Reviews")
        }

        composable(ScreenRoutes.HelperSettings.route) {
            PlaceholderScreen("Helper Settings")
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name)
    }
}
