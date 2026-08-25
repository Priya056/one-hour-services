package com.marketplace.onehour.customer.presentation.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.MockDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfirmationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConfirmationState())
    val uiState: StateFlow<ConfirmationState> = _uiState.asStateFlow()

    fun loadBookingConfirmation(bookingId: String) {
        viewModelScope.launch {
            val helper = MockDataProvider.sampleHelpers.first()
            _uiState.value = _uiState.value.copy(
                bookingId = bookingId,
                bookingReferenceCode = "#1H-${(100000..999999).random()}",
                helper = helper
            )
        }
    }
}
