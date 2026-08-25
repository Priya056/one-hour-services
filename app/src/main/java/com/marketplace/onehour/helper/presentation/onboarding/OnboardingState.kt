package com.marketplace.onehour.helper.presentation.onboarding

data class HelperBenefit(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String
)

data class OnboardingState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val benefits: List<HelperBenefit> = listOf(
        HelperBenefit("b1", "Set Your Own Hourly Rates", "Earn $25 - $100+/hr with full control over your service pricing.", "AttachMoney"),
        HelperBenefit("b2", "1-Hour Instant Jobs", "Accept bookings when you're free by toggling 'Available Now'.", "Schedule"),
        HelperBenefit("b3", "Instant Bank Payouts", "Withdraw your earnings directly to UPI or bank account anytime.", "AccountBalanceWallet"),
        HelperBenefit("b4", "Verified Customers", "Work safely with ID-verified local clients near your location.", "VerifiedUser")
    ),
    val isLoading: Boolean = false,
    val alreadyHelper: Boolean = false
)
