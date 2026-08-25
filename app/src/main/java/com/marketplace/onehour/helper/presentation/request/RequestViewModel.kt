package com.marketplace.onehour.helper.presentation.request

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BookingRequestItem(
    val bookingId: String,
    val customerName: String,
    val customerPhone: String,
    val serviceCategory: String,
    val distanceKm: String,
    val address: String,
    val totalAmount: String,
    val scheduledTime: String,
    val remainingSeconds: Int = 45
)

data class RequestState(
    val requests: List<BookingRequestItem> = listOf(
        BookingRequestItem(
            bookingId = "BK-8842",
            customerName = "Priya Sharma",
            customerPhone = "+91 98765 43210",
            serviceCategory = "Electrical Repair",
            distanceKm = "2.4 km away",
            address = "42, 10th Main Rd, Indiranagar, Bengaluru",
            totalAmount = "₹499.00",
            scheduledTime = "Today at 3:00 PM"
        ),
        BookingRequestItem(
            bookingId = "BK-8843",
            customerName = "Rahul Verma",
            customerPhone = "+91 98123 45678",
            serviceCategory = "Switchboard Installation",
            distanceKm = "3.8 km away",
            address = "105, 5th Cross, Koramangala, Bengaluru",
            totalAmount = "₹599.00",
            scheduledTime = "Today at 5:00 PM"
        )
    )
)

class RequestViewModel : ViewModel() {
    private val _state = MutableStateFlow(RequestState())
    val state: StateFlow<RequestState> = _state.asStateFlow()

    fun acceptRequest(bookingId: String) {
        _state.update { current ->
            current.copy(requests = current.requests.filterNot { it.bookingId == bookingId })
        }
    }

    fun declineRequest(bookingId: String) {
        _state.update { current ->
            current.copy(requests = current.requests.filterNot { it.bookingId == bookingId })
        }
    }
}
