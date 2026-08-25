package com.marketplace.onehour.customer.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.MockDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingState())
    val uiState: StateFlow<BookingState> = _uiState.asStateFlow()

    fun loadHelper(helperId: String) {
        viewModelScope.launch {
            val foundHelper = MockDataProvider.sampleHelpers.find { it.id == helperId }
                ?: MockDataProvider.sampleHelpers.first()

            val baseFee = foundHelper.hourlyRate
            val serviceFee = baseFee * 0.10
            val taxes = (baseFee + serviceFee) * 0.05
            val total = baseFee + serviceFee + taxes

            _uiState.value = _uiState.value.copy(
                helper = foundHelper,
                baseHourlyFee = baseFee,
                platformFee = serviceFee,
                taxAmount = taxes,
                totalAmount = total
            )
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
}
