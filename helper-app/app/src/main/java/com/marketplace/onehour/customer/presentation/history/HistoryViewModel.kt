package com.marketplace.onehour.customer.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.BookingDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private val COMPLETED_STATUSES = setOf("completed", "cancelled")

class HistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryState())
    val uiState: StateFlow<HistoryState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
    }

    fun refresh() = loadBookings()

    private fun loadBookings() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val bookings = ApiClient.api.getBookingsRaw().data.map { booking ->
                    BookingDto(
                        id = booking.id.toString(),
                        helperId = booking.helperId.toString(),
                        helperName = booking.helper?.user?.name ?: "Helper",
                        serviceName = booking.category?.name ?: "1-Hour Service",
                        status = booking.status.replace('_', ' ')
                            .replaceFirstChar { it.uppercase() },
                        scheduledTime = formatScheduledTime(booking.scheduledTime),
                        totalAmount = booking.totalAmount
                    )
                }
                _uiState.value = _uiState.value.copy(
                    upcomingBookings = bookings.filterNot { rawStatusOf(it) in COMPLETED_STATUSES },
                    pastBookings = bookings.filter { rawStatusOf(it) in COMPLETED_STATUSES },
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // BookingDto.status is already the display-formatted string by this point,
    // so recover the raw form to bucket upcoming vs past consistently.
    private fun rawStatusOf(booking: BookingDto): String =
        booking.status.lowercase().replace(' ', '_')

    private fun formatScheduledTime(isoTime: String): String = try {
        OffsetDateTime.parse(isoTime).format(DateTimeFormatter.ofPattern("d MMM, h:mm a"))
    } catch (e: Exception) {
        isoTime
    }
}
