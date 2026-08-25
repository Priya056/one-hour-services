package com.marketplace.onehour.customer.presentation.filter

data class FilterState(
    val selectedCategories: Set<String> = emptySet(),
    val minPrice: Float = 10f,
    val maxPrice: Float = 100f,
    val selectedPriceRange: ClosedFloatingPointRange<Float> = 10f..100f,
    val minRating: Float = 4.0f,
    val maxDistanceKm: Float = 10.0f,
    val isAvailableNowOnly: Boolean = true,
    val availableCategories: List<String> = listOf(
        "Personal Assistance",
        "Electrical",
        "Tutoring",
        "Photography",
        "Home Repairs",
        "Errands",
        "Design",
        "Business Assistance"
    )
)
