package com.marketplace.onehour.customer.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.MockDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrackingState())
    val uiState: StateFlow<TrackingState> = _uiState.asStateFlow()

    fun loadTrackingDetails(bookingId: String) {
        viewModelScope.launch {
            val helper = MockDataProvider.sampleHelpers.first()
            _uiState.value = _uiState.value.copy(
                bookingId = bookingId,
                helper = helper
            )
        }
    }

    fun advanceStatusSimulated() {
        val currentIdx = _uiState.value.status.index
        val nextIdx = (currentIdx + 1) % BookingStatus.values().size
        val nextStatus = BookingStatus.values()[nextIdx]
        _uiState.value = _uiState.value.copy(status = nextStatus)
    }
}
