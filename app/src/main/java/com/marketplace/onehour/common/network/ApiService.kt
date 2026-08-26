package com.marketplace.onehour.common.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class CategoryDto(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String
)

data class HelperDto(
    val id: String,
    val name: String,
    val photoUrl: String,
    val mainCategory: String,
    val rating: Double,
    val reviewCount: Int,
    val hourlyRate: Double,
    val distanceKm: Double,
    val bio: String,
    val skills: List<String>,
    val isAvailable: Boolean
)

data class BookingDto(
    val id: String,
    val helperId: String,
    val helperName: String,
    val customerId: String = "u101",
    val customerName: String = "Priya Sharma",
    val serviceName: String,
    val status: String, // Requested, Accepted, OnTheWay, InProgress, Completed, Cancelled
    val paymentStatus: String = "paid", // paid, pending, failed
    val scheduledTime: String,
    val totalAmount: Double, // Amount in INR (₹)
    val orderId: String? = null,
    val paymentId: String? = null
)

/**
 * Retrofit interface pointing to backend service endpoints.
 * TODO: Swap base URL and MockInterceptor with production backend API calls when ready.
 */
interface ApiService {
    @GET("api/v1/helpers/nearby")
    suspend fun getNearbyHelpers(
        @Query("category") category: String?,
        @Query("maxDistance") maxDistance: Double?
    ): List<HelperDto>

    @GET("api/v1/helpers/{id}")
    suspend fun getHelperProfile(@Path("id") helperId: String): HelperDto

    @GET("api/v1/bookings/user/{userId}")
    suspend fun getUserBookings(@Path("userId") userId: String): List<BookingDto>
}
