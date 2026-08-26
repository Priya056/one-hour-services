package com.marketplace.onehour.navigation

sealed class ScreenRoutes(val route: String) {
    // Customer Screens
    object CustomerSplash : ScreenRoutes("customer_splash")
    object CustomerAuth : ScreenRoutes("customer_auth")
    object CustomerHome : ScreenRoutes("customer_home")
    object CustomerFilter : ScreenRoutes("customer_filter")
    object HelperProfile : ScreenRoutes("helper_profile/{helperId}") {
        fun createRoute(helperId: String) = "helper_profile/$helperId"
    }
    object BookingFlow : ScreenRoutes("booking_flow/{helperId}") {
        fun createRoute(helperId: String) = "booking_flow/$helperId"
    }
    object Payment : ScreenRoutes("payment/{bookingId}") {
        fun createRoute(bookingId: String) = "payment/$bookingId"
    }
    object BookingConfirmation : ScreenRoutes("booking_confirmation/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_confirmation/$bookingId"
    }
    object LiveTracking : ScreenRoutes("live_tracking/{bookingId}") {
        fun createRoute(bookingId: String) = "live_tracking/$bookingId"
    }
    object Chat : ScreenRoutes("chat/{bookingId}/{helperId}") {
        fun createRoute(bookingId: String, helperId: String) = "chat/$bookingId/$helperId"
    }
    object BookingHistory : ScreenRoutes("booking_history")
    object RateReview : ScreenRoutes("rate_review/{bookingId}") {
        fun createRoute(bookingId: String) = "rate_review/$bookingId"
    }
    object CustomerSettings : ScreenRoutes("customer_settings")

    // Helper Screens
    object HelperOnboarding : ScreenRoutes("helper_onboarding")
    object HelperProfileCreation : ScreenRoutes("helper_profile_creation")
    object KycUpload : ScreenRoutes("kyc_upload")
    object HourlyRateSchedule : ScreenRoutes("hourly_rate_schedule")
    object HelperHome : ScreenRoutes("helper_home")
    object IncomingRequests : ScreenRoutes("incoming_requests")
    object ActiveBooking : ScreenRoutes("active_booking/{bookingId}") {
        fun createRoute(bookingId: String) = "active_booking/$bookingId"
    }
    object EarningsDashboard : ScreenRoutes("earnings_dashboard")
    object Wallet : ScreenRoutes("wallet")
    object TransactionHistory : ScreenRoutes("transaction_history")
    object HelperReviews : ScreenRoutes("helper_reviews")
    object HelperSettings : ScreenRoutes("helper_settings")
}
