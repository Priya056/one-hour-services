package com.marketplace.onehour.customer.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.UpdateBookingStatusRequestBody
import com.marketplace.onehour.common.network.resolveHelperDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Android's simplified customer-facing steps <-> real backend status enum.
private val BACKEND_STATUS = mapOf(
    BookingStatus.REQUESTED to "requested",
    BookingStatus.ACCEPTED to "accepted",
    BookingStatus.ON_THE_WAY to "on_the_way",
    BookingStatus.IN_PROGRESS to "in_progress",
    BookingStatus.COMPLETED to "completed"
)
private val FROM_BACKEND_STATUS = BACKEND_STATUS.entries.associate { (k, v) -> v to k }

class TrackingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrackingState())
    val uiState: StateFlow<TrackingState> = _uiState.asStateFlow()

    fun loadTrackingDetails(bookingId: String) {
        val bookingIdInt = bookingId.toIntOrNull()
        if (bookingIdInt == null) {
            _uiState.value = _uiState.value.copy(bookingId = bookingId)
            return
        }

        viewModelScope.launch {
            try {
                val booking = ApiClient.api.getBooking(bookingIdInt).data
                _uiState.value = _uiState.value.copy(
                    bookingId = bookingId,
                    helper = booking.resolveHelperDisplay(),
                    status = FROM_BACKEND_STATUS[booking.status] ?: BookingStatus.REQUESTED
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(bookingId = bookingId)
            }
        }
    }

    /**
     * Advances the real booking status via PATCH /bookings/{id}/status,
     * following the backend's own state machine. Stops at COMPLETED instead
     * of wrapping back to REQUESTED (previous bug).
     */
    fun advanceStatusSimulated() {
        val current = _uiState.value.status
        if (current == BookingStatus.COMPLETED) return

        val nextIdx = current.index + 1
        val nextStatus = BookingStatus.values().getOrNull(nextIdx) ?: return
        val backendStatus = BACKEND_STATUS[nextStatus] ?: return
        val bookingIdInt = _uiState.value.bookingId.toIntOrNull() ?: return

        viewModelScope.launch {
            try {
                ApiClient.api.updateBookingStatus(bookingIdInt, UpdateBookingStatusRequestBody(status = backendStatus))
                _uiState.value = _uiState.value.copy(status = nextStatus)
            } catch (e: Exception) {
                // Leave status as-is; the demo "advance" tap just has no effect if the API call fails.
            }
        }
    }
}
