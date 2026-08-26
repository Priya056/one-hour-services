package com.marketplace.onehour.customer.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.CreateBookingRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingState())
    val uiState: StateFlow<BookingState> = _uiState.asStateFlow()

    fun loadHelper(helperId: Int) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val helper = ApiClient.apiService.getHelperProfile(helperId)
                
                val baseFee = helper.hourly_rate ?: 35.0
                val serviceFee = baseFee * 0.10
                val taxes = (baseFee + serviceFee) * 0.05
                val total = baseFee + serviceFee + taxes

                _uiState.value = _uiState.value.copy(
                    helper = helper,
                    baseHourlyFee = baseFee,
                    platformFee = serviceFee,
                    taxAmount = taxes,
                    totalAmount = total,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load helper: ${e.message}"
                )
            }
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

    fun createBooking(
        onSuccess: (bookingId: Int) -> Unit
    ) {
        val helper = _uiState.value.helper
        if (helper == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Helper not loaded")
            return
        }

        _uiState.value = _uiState.value.copy(isCreatingBooking = true, errorMessage = null)
        viewModelScope.launch {
            try {
                // Parse scheduled time from UI state
                val scheduledTime = parseScheduledTime(
                    _uiState.value.selectedDate,
                    _uiState.value.selectedTimeSlot,
                    _uiState.value.isInstantBooking
                )

                val request = CreateBookingRequest(
                    helper_id = helper.id,
                    category_id = helper.category?.id ?: 1,
                    scheduled_time = scheduledTime,
                    duration_hours = 1.0, // Default 1 hour
                    location_lat = 12.9716, // TODO: Use actual user location
                    location_lng = 77.5946,
                    address_text = _uiState.value.selectedAddress
                )

                val booking = ApiClient.apiService.createBooking(request)
                
                _uiState.value = _uiState.value.copy(
                    isCreatingBooking = false,
                    createdBookingId = booking.id
                )
                
                onSuccess(booking.id)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreatingBooking = false,
                    errorMessage = "Failed to create booking: ${e.message}"
                )
            }
        }
    }

    private fun parseScheduledTime(date: String, timeSlot: String, isInstant: Boolean): String {
        return if (isInstant) {
            // For instant booking, schedule 15 minutes from now
            val now = LocalDateTime.now().plusMinutes(15)
            now.format(DateTimeFormatter.ISO_DATE_TIME)
        } else {
            // Parse the date and time slot
            // Format: "Today, Aug 24" + "02:00 PM - 03:00 PM"
            // Simplified parsing for demo
            val now = LocalDateTime.now()
            val hour = timeSlot.substringBefore(":").toIntOrNull() ?: 14
            val time = now.withHour(hour).withMinute(0).withSecond(0)
            time.format(DateTimeFormatter.ISO_DATE_TIME)
        }
    }
}
