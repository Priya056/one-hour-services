package com.marketplace.onehour.common.network

/**
 * Resolves the HelperDto to show for a fetched booking. Prefers the cache
 * populated by Home's nearby search (has real photo/rating/hourlyRate/bio);
 * falls back to building a minimal one from the booking's own nested helper
 * so this still works if the app was killed and HelperRepository is empty.
 */
fun BookingApiDto.resolveHelperDisplay(): HelperDto? {
    HelperRepository.findById(helperId.toString())?.let { return it }

    val profile = helper ?: return null
    return HelperDto(
        id = profile.id.toString(),
        name = profile.user?.name ?: "Helper",
        photoUrl = profile.user?.profilePhotoUrl ?: "",
        mainCategory = category?.name ?: "",
        categoryId = categoryId.toString(),
        rating = profile.averageRating,
        reviewCount = profile.totalReviews,
        hourlyRate = totalAmount,
        distanceKm = 0.0,
        bio = profile.bio ?: "",
        skills = emptyList(),
        isAvailable = profile.isAvailableNow
    )
}
