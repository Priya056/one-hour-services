package com.marketplace.onehour.customer.presentation.booking

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.location.LocationProvider
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.CreateBookingRequestBody
import com.marketplace.onehour.common.network.HelperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// The 3 fixed "Schedule Later" slot chips shown in BookingScreen — mapped to a
// 24h start hour so a real scheduled_time can be built for the API.
private val SCHEDULED_SLOT_START_HOUR = mapOf(
    "02:00 - 03:00 PM" to 14,
    "04:00 - 05:00 PM" to 16,
    "06:00 - 07:00 PM" to 18
)

class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingState())
    val uiState: StateFlow<BookingState> = _uiState.asStateFlow()

    fun loadHelper(helperId: String) {
        viewModelScope.launch {
            val foundHelper = HelperRepository.findById(helperId) ?: HelperRepository.all().firstOrNull()

            if (foundHelper == null) {
                _uiState.value = _uiState.value.copy(bookingError = "Helper not found — go back and search again.")
                return@launch
            }

            val baseFee = foundHelper.hourlyRate
            val serviceFee = baseFee * 0.10
            val taxes = (baseFee + serviceFee) * 0.05
            val total = baseFee + serviceFee + taxes

            _uiState.value = _uiState.value.copy(
                helper = foundHelper,
                baseHourlyFee = baseFee,
                platformFee = serviceFee,
                taxAmount = taxes,
                totalAmount = total
            )
        }
    }

    fun setInstantBooking(isInstant: Boolean) {
        val slot = if (isInstant) "Now (Arrive in ~15 mins)" else "02:00 PM - 03:00 PM"
        _uiState.value = _uiState.value.copy(
            isInstantBooking = isInstant,
            selectedTimeSlot = slot
        )
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun selectTimeSlot(slot: String) {
        _uiState.value = _uiState.value.copy(selectedTimeSlot = slot)
    }

    fun onInstructionsChanged(instructions: String) {
        _uiState.value = _uiState.value.copy(specialInstructions = instructions)
    }

    /**
     * Creates the real booking via POST /bookings. helper_id/category_id come
     * from the helper the user actually tapped through from Home; scheduled_time
     * is computed from the Instant/Schedule selection (must satisfy the
     * backend's "after:now" validation).
     */
    fun confirmBooking(context: Context, onSuccess: (bookingId: String) -> Unit) {
        val helper = _uiState.value.helper
        val helperIdInt = helper?.id?.toIntOrNull()
        val categoryIdInt = helper?.categoryId?.toIntOrNull()

        if (helper == null || helperIdInt == null || categoryIdInt == null) {
            _uiState.value = _uiState.value.copy(bookingError = "Missing helper details — go back and reselect.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, bookingError = null)
        viewModelScope.launch {
            try {
                val device = LocationProvider.getCurrentLocation(context)
                val response = ApiClient.api.createBooking(
                    CreateBookingRequestBody(
                        helperId = helperIdInt,
                        categoryId = categoryIdInt,
                        scheduledTime = computeScheduledTimeIso(),
                        durationHours = 1.0,
                        locationLat = device.lat,
                        locationLng = device.lng,
                        addressText = _uiState.value.selectedAddress
                    )
                )
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess(response.data.id.toString())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    bookingError = "Couldn't create booking: ${e.message}"
                )
            }
        }
    }

    private fun computeScheduledTimeIso(): String {
        val now = LocalDateTime.now()
        val target = if (_uiState.value.isInstantBooking) {
            // Comfortably clears the backend's "after:now" check for an
            // "arrive in ~15 min" instant booking.
            now.plusMinutes(20)
        } else {
            val hour = SCHEDULED_SLOT_START_HOUR[_uiState.value.selectedTimeSlot] ?: 14
            now.plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0)
        }
        return target.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    }
}
