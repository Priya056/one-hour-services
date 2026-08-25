package com.marketplace.onehour.navigation

import androidx.compose.runtime.Composable
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
import com.marketplace.onehour.helper.presentation.activejob.HelperActiveJobScreen
import com.marketplace.onehour.helper.presentation.dashboard.HelperDashboardHomeScreen
import com.marketplace.onehour.helper.presentation.history.HelperJobHistoryScreen
import com.marketplace.onehour.helper.presentation.kyc.KycUploadScreen
import com.marketplace.onehour.helper.presentation.onboarding.BecomeHelperOnboardingScreen
import com.marketplace.onehour.helper.presentation.profile.HelperProfileCreationScreen
import com.marketplace.onehour.helper.presentation.request.IncomingBookingRequestScreen
import com.marketplace.onehour.helper.presentation.reviews.HelperReviewsRatingsScreen
import com.marketplace.onehour.helper.presentation.schedule.HourlyRateScheduleScreen
import com.marketplace.onehour.helper.presentation.settings.HelperSettingsSupportScreen
import com.marketplace.onehour.helper.presentation.wallet.HelperEarningsWalletScreen
import com.marketplace.onehour.helper.presentation.wallet.WithdrawalRequestScreen

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

        // Helper Routes (Complete 12-Screen Suite)
        composable(ScreenRoutes.HelperOnboarding.route) {
            BecomeHelperOnboardingScreen(
                onBackClick = { navController.popBackStack() },
                onGetStartedClick = {
                    navController.navigate(ScreenRoutes.HelperProfileCreation.route)
                }
            )
        }

        composable(ScreenRoutes.HelperProfileCreation.route) {
            HelperProfileCreationScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = {
                    navController.navigate(ScreenRoutes.KycUpload.route)
                }
            )
        }

        composable(ScreenRoutes.KycUpload.route) {
            KycUploadScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = {
                    navController.navigate(ScreenRoutes.HourlyRateSchedule.route)
                }
            )
        }

        composable(ScreenRoutes.HourlyRateSchedule.route) {
            HourlyRateScheduleScreen(
                onBackClick = { navController.popBackStack() },
                onCompleteRegistration = {
                    navController.navigate(ScreenRoutes.HelperHome.route) {
                        popUpTo(ScreenRoutes.CustomerHome.route)
                    }
                }
            )
        }

        composable(ScreenRoutes.HelperHome.route) {
            HelperDashboardHomeScreen(
                onNavigateToRequests = { navController.navigate(ScreenRoutes.IncomingRequests.route) },
                onNavigateToActiveJob = { bId -> navController.navigate(ScreenRoutes.ActiveBooking.createRoute(bId)) },
                onNavigateToWallet = { navController.navigate(ScreenRoutes.Wallet.route) },
                onNavigateToReviews = { navController.navigate(ScreenRoutes.HelperReviews.route) },
                onNavigateToSettings = { navController.navigate(ScreenRoutes.HelperSettings.route) }
            )
        }

        composable(ScreenRoutes.IncomingRequests.route) {
            IncomingBookingRequestScreen(
                onNavigateToActiveJob = { bId -> navController.navigate(ScreenRoutes.ActiveBooking.createRoute(bId)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoutes.ActiveBooking.route) { backStack ->
            val bookingId = backStack.arguments?.getString("bookingId") ?: "BK-8842"
            HelperActiveJobScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() },
                onJobCompleted = { navController.navigate(ScreenRoutes.Wallet.route) }
            )
        }

        composable(ScreenRoutes.Wallet.route) {
            HelperEarningsWalletScreen(
                onNavigateToWithdrawal = { navController.navigate(ScreenRoutes.TransactionHistory.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoutes.TransactionHistory.route) {
            WithdrawalRequestScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoutes.EarningsDashboard.route) {
            HelperJobHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoutes.HelperReviews.route) {
            HelperReviewsRatingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ScreenRoutes.HelperSettings.route) {
            HelperSettingsSupportScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(ScreenRoutes.CustomerHome.route) {
                        popUpTo(ScreenRoutes.HelperHome.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
