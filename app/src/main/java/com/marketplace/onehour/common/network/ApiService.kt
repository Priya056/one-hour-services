package com.marketplace.onehour.common.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// ---- UI-facing display models (kept stable so existing screens don't need
// to change how they bind fields; ViewModels map real API DTOs onto these) ----
data class HelperDto(
    val id: String,
    val name: String,
    val photoUrl: String,
    val mainCategory: String,
    val categoryId: String,
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
    val serviceName: String,
    val status: String, // Requested, Accepted, OnTheWay, InProgress, Completed
    val scheduledTime: String,
    val totalAmount: Double
)

// ---- Generic Laravel Resource envelopes ----
data class DataWrapper<T>(val data: T)
data class DataListWrapper<T>(val data: List<T>)

// ---- Real backend DTOs (field names map to snake_case JSON automatically
// via ApiClient's Gson naming policy) ----
data class UserDto(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String?,
    val role: String,
    val profilePhotoUrl: String?,
    val address: String?,
    val isActive: Boolean
)

data class AuthResponse(
    val user: UserDto,
    val token: String
)

data class CategoryDto(
    val id: Int,
    val name: String,
    val iconUrl: String?,
    val description: String?
)

data class NearbyHelperDto(
    val id: Int,
    val name: String,
    val profilePhotoUrl: String?,
    val bio: String?,
    val category: CategoryDto?,
    val hourlyRate: Double?,
    val averageRating: Double,
    val totalReviews: Int,
    val distanceKm: Double
)

data class HelperProfileDto(
    val id: Int,
    val bio: String?,
    val experienceYears: Int,
    val isAvailableNow: Boolean,
    val serviceRadiusKm: Double,
    val averageRating: Double,
    val totalReviews: Int,
    val kycStatus: String,
    val user: UserDto?
)

data class KycDocumentDto(
    val id: Int,
    val documentType: String,
    val documentUrl: String,
    val status: String
)

data class HelperServiceDto(
    val id: Int,
    val hourlyRate: Double,
    val isActive: Boolean,
    val category: CategoryDto?
)

data class BookingApiDto(
    val id: Int,
    val customerId: Int,
    val helperId: Int,
    val categoryId: Int,
    val scheduledTime: String,
    val durationHours: Double,
    val status: String,
    val locationLat: Double,
    val locationLng: Double,
    val addressText: String,
    val totalAmount: Double,
    val customer: UserDto? = null,
    val helper: HelperProfileDto? = null,
    val category: CategoryDto? = null
)

data class ReviewDto(
    val id: Int,
    val bookingId: Int,
    val customerId: Int,
    val helperId: Int,
    val rating: Int,
    val comment: String?,
    val createdAt: String? = null,
    val customer: UserDto? = null
)

// ---- Request bodies ----
data class RegisterRequestBody(val name: String, val phone: String, val email: String?, val password: String, val role: String)
data class LoginRequestBody(val phone: String, val password: String)
data class BecomeHelperRequestBody(val bio: String?, val experienceYears: Int?, val serviceRadiusKm: Double?)
data class KycSubmitRequestBody(val documentType: String, val documentUrl: String)
data class HelperServiceRequestBody(val categoryId: Int, val hourlyRate: Double)
data class UpdateLocationRequestBody(val lat: Double, val lng: Double)
data class ToggleAvailableRequestBody(val isAvailableNow: Boolean)
data class CreateBookingRequestBody(
    val helperId: Int,
    val categoryId: Int,
    val scheduledTime: String,
    val durationHours: Double,
    val locationLat: Double,
    val locationLng: Double,
    val addressText: String
)
data class UpdateBookingStatusRequestBody(val status: String)
data class CreateReviewRequestBody(val bookingId: Int, val helperId: Int, val rating: Int, val comment: String?)
data class CreateAvailabilityRequestBody(val dayOfWeek: Int, val startTime: String, val endTime: String)
data class AvailabilityDto(val id: Int, val dayOfWeek: Int, val startTime: String, val endTime: String)

data class WalletTransactionDto(
    val id: Int,
    val type: String,
    val amount: Double,
    val status: String,
    val bookingId: Int?,
    val createdAt: String
)
data class WalletDto(
    val id: Int,
    val balance: Double,
    val transactions: List<WalletTransactionDto>
)
data class WithdrawRequestBody(val amount: Double, val bankAccountDetails: Map<String, String>)
data class WithdrawResponse(val message: String, val withdrawalRequestId: Int, val amount: Double, val status: String)

/**
 * Real backend endpoints. Base URL / auth header are handled by ApiClient.
 * No /v1 prefix, snake_case JSON — see ApiClient's Gson config.
 */
interface ApiService {
    @POST("api/register")
    suspend fun register(@Body body: RegisterRequestBody): AuthResponse

    @POST("api/login")
    suspend fun login(@Body body: LoginRequestBody): AuthResponse

    @POST("api/logout")
    suspend fun logout()

    @GET("api/categories")
    suspend fun getCategories(): DataListWrapper<CategoryDto>

    @GET("api/helpers/nearby")
    suspend fun getNearbyHelpersRaw(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("category_id") categoryId: Int?,
        @Query("max_distance_km") maxDistanceKm: Double?
    ): DataListWrapper<NearbyHelperDto>

    @POST("api/helper/profile")
    suspend fun becomeHelper(@Body body: BecomeHelperRequestBody): DataWrapper<HelperProfileDto>

    @GET("api/helper/profile")
    suspend fun getHelperProfile(): DataWrapper<HelperProfileDto>

    @POST("api/kyc")
    suspend fun submitKyc(@Body body: KycSubmitRequestBody): DataWrapper<KycDocumentDto>

    @POST("api/helper/services")
    suspend fun addHelperService(@Body body: HelperServiceRequestBody): DataWrapper<HelperServiceDto>

    @PATCH("api/helper/location")
    suspend fun updateHelperLocation(@Body body: UpdateLocationRequestBody)

    @PATCH("api/helper/available-now")
    suspend fun setAvailableNow(@Body body: ToggleAvailableRequestBody)

    @POST("api/helper/availability")
    suspend fun createAvailability(@Body body: CreateAvailabilityRequestBody): DataWrapper<AvailabilityDto>

    @POST("api/bookings")
    suspend fun createBooking(@Body body: CreateBookingRequestBody): DataWrapper<BookingApiDto>

    @GET("api/bookings")
    suspend fun getBookingsRaw(): DataListWrapper<BookingApiDto>

    @GET("api/bookings/{id}")
    suspend fun getBooking(@Path("id") id: Int): DataWrapper<BookingApiDto>

    @PATCH("api/bookings/{id}/status")
    suspend fun updateBookingStatus(@Path("id") id: Int, @Body body: UpdateBookingStatusRequestBody): DataWrapper<BookingApiDto>

    @PATCH("api/bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: Int): DataWrapper<BookingApiDto>

    @POST("api/reviews")
    suspend fun createReview(@Body body: CreateReviewRequestBody): DataWrapper<ReviewDto>

    @GET("api/helpers/{helperId}/reviews")
    suspend fun getHelperReviews(@Path("helperId") helperId: Int): DataListWrapper<ReviewDto>

    @GET("api/wallet")
    suspend fun getWallet(): DataWrapper<WalletDto>

    @POST("api/wallet/withdraw")
    suspend fun withdraw(@Body body: WithdrawRequestBody): WithdrawResponse
}
