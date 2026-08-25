package com.marketplace.onehour.customer.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.BookingDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryState())
    val uiState: StateFlow<HistoryState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
    }

    private fun loadBookings() {
        viewModelScope.launch {
            val upcoming = listOf(
                BookingDto(
                    id = "b101",
                    helperId = "h1",
                    helperName = "Alex Rivera",
                    serviceName = "Electrical Inspection & Socket Fix",
                    status = "On the way",
                    scheduledTime = "Today, 02:00 PM",
                    totalAmount = 40.0
                )
            )

            val past = listOf(
                BookingDto(
                    id = "b102",
                    helperId = "h2",
                    helperName = "Sarah Jenkins",
                    serviceName = "Grocery & Document Errands",
                    status = "Completed",
                    scheduledTime = "Yesterday, 11:00 AM",
                    totalAmount = 28.0
                ),
                BookingDto(
                    id = "b103",
                    helperId = "h4",
                    helperName = "David Chen",
                    serviceName = "Physics & Math 1-Hour Crash Course",
                    status = "Completed",
                    scheduledTime = "15 Aug 2026, 04:00 PM",
                    totalAmount = 40.0
                )
            )

            _uiState.value = _uiState.value.copy(
                upcomingBookings = upcoming,
                pastBookings = past
            )
        }
    }
}
