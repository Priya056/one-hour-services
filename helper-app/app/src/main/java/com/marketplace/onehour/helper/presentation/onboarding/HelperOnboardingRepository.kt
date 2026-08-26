package com.marketplace.onehour.helper.presentation.onboarding

/**
 * The 4 onboarding screens (Hero, Profile, KYC, Schedule) each have their own
 * ViewModel scoped to that screen — Compose Navigation gives each a separate
 * instance. This in-memory holder is how data collected on earlier steps
 * reaches the final "Complete Registration" submission on the Schedule step.
 */
object HelperOnboardingRepository {
    var categoryId: Int? = null
    var bio: String = ""
    var experienceYears: Int = 0
    var kycDocumentType: String = ""
    var kycDocumentUrl: String = ""

    fun reset() {
        categoryId = null
        bio = ""
        experienceYears = 0
        kycDocumentType = ""
        kycDocumentUrl = ""
    }
}
