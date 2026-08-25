package com.marketplace.onehour.helper.presentation.schedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleState())
    val uiState: StateFlow<ScheduleState> = _uiState.asStateFlow()

    fun onRateChanged(rate: Double) {
        _uiState.value = _uiState.value.copy(hourlyRate = rate)
    }

    fun toggleInstantBooking(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(instantBookingEnabled = enabled)
    }

    fun toggleDay(day: String) {
        val current = _uiState.value.selectedDays
        val updated = if (current.contains(day)) current - day else current + day
        _uiState.value = _uiState.value.copy(selectedDays = updated)
    }

    fun toggleTimeSlot(slot: String) {
        val current = _uiState.value.selectedTimeSlots
        val updated = if (current.contains(slot)) current - slot else current + slot
        _uiState.value = _uiState.value.copy(selectedTimeSlots = updated)
    }
}
