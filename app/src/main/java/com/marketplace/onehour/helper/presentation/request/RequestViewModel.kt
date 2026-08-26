package com.marketplace.onehour.helper.presentation.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.UpdateBookingStatusRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class BookingRequestItem(
    val bookingId: String,
    val customerName: String,
    val serviceCategory: String,
    val address: String,
    val totalAmount: String,
    val scheduledTime: String
)

data class RequestState(
    val requests: List<BookingRequestItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RequestViewModel : ViewModel() {
    private val _state = MutableStateFlow(RequestState())
    val state: StateFlow<RequestState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val requested = ApiClient.api.getBookingsRaw().data
                    .filter { it.status == "requested" }
                    .map {
                        BookingRequestItem(
                            bookingId = it.id.toString(),
                            customerName = it.customer?.name.orEmpty(),
                            serviceCategory = it.category?.name.orEmpty(),
                            address = it.addressText,
                            totalAmount = "₹%.2f".format(it.totalAmount),
                            scheduledTime = formatScheduledTime(it.scheduledTime)
                        )
                    }
                _state.update { it.copy(requests = requested, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Couldn't load requests: ${e.message}") }
            }
        }
    }

    fun acceptRequest(bookingId: String, onAccepted: () -> Unit) {
        val id = bookingId.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.updateBookingStatus(id, UpdateBookingStatusRequestBody(status = "accepted"))
                _state.update { current ->
                    current.copy(requests = current.requests.filterNot { it.bookingId == bookingId })
                }
                onAccepted()
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Couldn't accept job: ${e.message}") }
            }
        }
    }

    fun declineRequest(bookingId: String) {
        val id = bookingId.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                ApiClient.api.cancelBooking(id)
                _state.update { current ->
                    current.copy(requests = current.requests.filterNot { it.bookingId == bookingId })
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Couldn't decline job: ${e.message}") }
            }
        }
    }

    private fun formatScheduledTime(isoTime: String): String = try {
        OffsetDateTime.parse(isoTime).format(DateTimeFormatter.ofPattern("d MMM, h:mm a"))
    } catch (e: Exception) {
        isoTime
    }
}
