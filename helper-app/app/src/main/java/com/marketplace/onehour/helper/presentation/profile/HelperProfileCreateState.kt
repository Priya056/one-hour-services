package com.marketplace.onehour.helper.presentation.profile

import com.marketplace.onehour.common.network.CategoryDto

data class HelperProfileCreateState(
    val photoUri: String? = null,
    val categories: List<CategoryDto> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedCategoryName: String = "",
    val availableSkills: List<String> = listOf(
        "Circuit Breaker Repair",
        "Outlet & Switch Wiring",
        "Appliance Hookup",
        "Lighting Installation",
        "Emergency Troubleshooting",
        "Fan & Fixture Mounting"
    ),
    val selectedSkills: Set<String> = emptySet(),
    val experienceYears: String = "",
    val bioText: String = "",
    val isLoading: Boolean = false
)
