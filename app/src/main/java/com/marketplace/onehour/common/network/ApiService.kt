package com.marketplace.onehour.common.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Data models matching Laravel backend API responses
data class CategoryDto(
    val id: Int,
    val name: String,
    val icon_url: String?,
    val description: String,
    val created_at: String,
    val updated_at: String
)

data class HelperDto(
    val id: Int,
    val name: String,
    val profile_photo_url: String?,
    val bio: String,
    val category: CategoryDto?,
    val hourly_rate: Double?,
    val average_rating: Double,
    val total_reviews: Int,
    val distance_km: Double
)

data class BookingDto(
    val id: Int,
    val customer_id: Int,
    val helper_id: Int,
    val category_id: Int,
    val scheduled_time: String,
    val duration_hours: Double,
    val status: String,
    val location_lat: Double,
    val location_lng: Double,
    val address_text: String,
    val total_amount: Double,
    val created_at: String,
    val updated_at: String
)

data class CreateBookingRequest(
    val helper_id: Int,
    val category_id: Int,
    val scheduled_time: String,
    val duration_hours: Double,
    val location_lat: Double,
    val location_lng: Double,
    val address_text: String
)

data class ApiError(
    val message: String,
    val errors: Map<String, List<String>>? = null
)

data class FirebaseLoginRequest(
    val id_token: String
)

data class FirebaseLoginResponse(
    val token: String,
    val user: UserSummary?
)

data class UserSummary(
    val id: Int,
    val name: String,
    val email: String?,
    val role: String
)

/**
 * Retrofit interface pointing to Laravel backend service endpoints.
 * Base URL should be configured in ApiClient.
 */
interface ApiService {
    @POST("api/auth/firebase-login")
    suspend fun firebaseLogin(@Body request: FirebaseLoginRequest): FirebaseLoginResponse

    @GET("api/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("api/categories/{id}")
    suspend fun getCategory(@Path("id") categoryId: Int): CategoryDto

    @GET("api/helpers/nearby")
    suspend fun getNearbyHelpers(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("category_id") categoryId: Int?,
        @Query("max_distance_km") maxDistanceKm: Double?
    ): List<HelperDto>

    @GET("api/helpers/{id}")
    suspend fun getHelperProfile(@Path("id") helperId: Int): HelperDto

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): BookingDto

    @GET("api/bookings")
    suspend fun getUserBookings(): List<BookingDto>

    @GET("api/bookings/{id}")
    suspend fun getBooking(@Path("id") bookingId: Int): BookingDto
}
