package com.marketplace.onehour.common.network

import android.util.Log

object NetworkRepository {
    private val apiService = RetrofitClient.instance

    suspend fun fetchNearbyHelpers(category: String? = null, maxDistance: Double? = null): List<HelperDto> {
        return try {
            val response = apiService.getNearbyHelpers(category, maxDistance)
            Log.d("NetworkRepository", "Fetched ${response.size} helpers from live API backend.")
            response.ifEmpty { MockDataProvider.sampleHelpers }
        } catch (e: Exception) {
            Log.w("NetworkRepository", "Backend REST API unavailable (${e.localizedMessage}). Using MockDataProvider fallback.")
            MockDataProvider.sampleHelpers
        }
    }

    suspend fun fetchHelperProfile(helperId: String): HelperDto {
        return try {
            apiService.getHelperProfile(helperId)
        } catch (e: Exception) {
            Log.w("NetworkRepository", "Backend API unavailable for profile $helperId. Using MockDataProvider fallback.")
            MockDataProvider.sampleHelpers.find { it.id == helperId } ?: MockDataProvider.sampleHelpers.first()
        }
    }

    suspend fun fetchUserBookings(userId: String): List<BookingDto> {
        return try {
            apiService.getUserBookings(userId)
        } catch (e: Exception) {
            Log.w("NetworkRepository", "Backend API unavailable for user bookings. Using mock fallback.")
            emptyList()
        }
    }
}
