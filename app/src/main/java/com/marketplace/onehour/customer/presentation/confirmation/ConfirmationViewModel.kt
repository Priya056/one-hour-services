package com.marketplace.onehour.customer.presentation.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.resolveHelperDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfirmationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConfirmationState())
    val uiState: StateFlow<ConfirmationState> = _uiState.asStateFlow()

    fun loadBookingConfirmation(bookingId: String) {
        val bookingIdInt = bookingId.toIntOrNull()
        if (bookingIdInt == null) {
            _uiState.value = _uiState.value.copy(bookingId = bookingId, bookingReferenceCode = "#1H-$bookingId")
            return
        }

        viewModelScope.launch {
            try {
                val booking = ApiClient.api.getBooking(bookingIdInt).data
                _uiState.value = _uiState.value.copy(
                    bookingId = bookingId,
                    bookingReferenceCode = "#1H-$bookingId",
                    helper = booking.resolveHelperDisplay(),
                    totalPaid = booking.totalAmount,
                    serviceAddress = booking.addressText
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(bookingId = bookingId, bookingReferenceCode = "#1H-$bookingId")
            }
        }
    }
}
