package com.marketplace.onehour.helper.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.ToggleAvailableRequestBody
import com.marketplace.onehour.common.network.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class DashboardState(
    val isAvailableNow: Boolean = false,
    val helperName: String = "",
    val todayEarnings: String = "₹0.00",
    val completedJobsToday: Int = 0,
    val averageRating: Double = 0.0,
    val pendingRequestsCount: Int = 0,
    val activeBookingId: String? = null,
    val activeCustomerName: String? = null,
    val activeServiceTime: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState(helperName = TokenStore.getName().orEmpty()))
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val profile = ApiClient.api.getHelperProfile().data
                val bookings = ApiClient.api.getBookingsRaw().data
                val today = LocalDate.now()

                val completedToday = bookings.filter {
                    it.status == "completed" && isSameDay(it.scheduledTime, today)
                }
                val earningsToday = completedToday.sumOf { it.totalAmount }
                val pendingCount = bookings.count { it.status == "requested" }
                val active = bookings
                    .filter { it.status in listOf("accepted", "on_the_way", "in_progress") }
                    .minByOrNull { it.scheduledTime }

                _state.update {
                    it.copy(
                        isAvailableNow = profile.isAvailableNow,
                        helperName = TokenStore.getName().orEmpty(),
                        todayEarnings = "₹%.2f".format(earningsToday),
                        completedJobsToday = completedToday.size,
                        averageRating = profile.averageRating,
                        pendingRequestsCount = pendingCount,
                        activeBookingId = active?.id?.toString(),
                        activeCustomerName = active?.customer?.name,
                        activeServiceTime = active?.scheduledTime?.let(::formatScheduledTime),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Couldn't load dashboard: ${e.message}") }
            }
        }
    }

    fun toggleAvailability(isAvailable: Boolean) {
        val previous = _state.value.isAvailableNow
        _state.update { it.copy(isAvailableNow = isAvailable) }
        viewModelScope.launch {
            try {
                ApiClient.api.setAvailableNow(ToggleAvailableRequestBody(isAvailableNow = isAvailable))
            } catch (e: Exception) {
                _state.update { it.copy(isAvailableNow = previous, errorMessage = "Couldn't update availability: ${e.message}") }
            }
        }
    }

    private fun isSameDay(isoTime: String, day: LocalDate): Boolean = try {
        OffsetDateTime.parse(isoTime).toLocalDate() == day
    } catch (e: Exception) {
        false
    }

    private fun formatScheduledTime(isoTime: String): String = try {
        OffsetDateTime.parse(isoTime).format(DateTimeFormatter.ofPattern("d MMM, h:mm a"))
    } catch (e: Exception) {
        isoTime
    }
}
