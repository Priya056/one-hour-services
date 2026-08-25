package com.marketplace.onehour.helper.presentation.profile

data class HelperProfileCreateState(
    val photoUri: String? = null,
    val selectedCategory: String = "Electrical Specialist",
    val availableSkills: List<String> = listOf(
        "Circuit Breaker Repair",
        "Outlet & Switch Wiring",
        "Appliance Hookup",
        "Lighting Installation",
        "Emergency Troubleshooting",
        "Fan & Fixture Mounting"
    ),
    val selectedSkills: Set<String> = setOf("Circuit Breaker Repair", "Outlet & Switch Wiring"),
    val experienceYears: String = "4 Years",
    val bioText: String = "Licensed electrician with 4 years experience in residential and commercial wiring. Focused on prompt, reliable 1-hour service.",
    val isLoading: Boolean = false
)
