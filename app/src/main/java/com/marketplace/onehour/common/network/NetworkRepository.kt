package com.marketplace.onehour.common.network

import android.util.Log

object NetworkRepository {
    private val apiService = RetrofitClient.instance

    suspend fun fetchNearbyHelpers(category: String? = null, maxDistance: Double? = null): List<HelperDto> {
        return try {
            val helpers = MockDataProvider.sampleHelpers
            if (!category.isNull_or_empty()) {
                helpers.filter { it.mainCategory.contains(category, ignoreCase = true) }
            } else {
                helpers
            }
        } catch (e: Exception) {
            Log.w("NetworkRepository", "Error fetching helpers (${e.localizedMessage}). Using MockDataProvider fallback.")
            MockDataProvider.sampleHelpers
        }
    }

    suspend fun fetchHelperProfile(helperId: String): HelperDto {
        return MockDataProvider.sampleHelpers.find { it.id == helperId } ?: MockDataProvider.sampleHelpers.first()
    }

    suspend fun fetchUserBookings(userId: String): List<BookingDto> {
        return MockDataProvider.sampleBookings
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()

