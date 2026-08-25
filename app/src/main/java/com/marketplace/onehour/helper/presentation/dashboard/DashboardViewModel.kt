package com.marketplace.onehour.helper.presentation.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DashboardState(
    val isAvailableNow: Boolean = true,
    val helperName: String = "Vikram Singh",
    val serviceCategory: String = "Electrician",
    val todayEarnings: String = "₹2,450.00",
    val completedJobsToday: Int = 4,
    val averageRating: Double = 4.9,
    val pendingRequestsCount: Int = 2,
    val activeBookingId: String? = "BK-8842",
    val activeCustomerName: String? = "Priya Sharma",
    val activeServiceTime: String? = "Scheduled at 3:00 PM (In 20 mins)"
)

class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun toggleAvailability(isAvailable: Boolean) {
        _state.update { it.copy(isAvailableNow = isAvailable) }
    }
}
