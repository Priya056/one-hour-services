package com.marketplace.onehour.customer.presentation.filter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FilterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FilterState())
    val uiState: StateFlow<FilterState> = _uiState.asStateFlow()

    fun toggleCategory(category: String) {
        val current = _uiState.value.selectedCategories
        val updated = if (current.contains(category)) {
            current - category
        } else {
            current + category
        }
        _uiState.value = _uiState.value.copy(selectedCategories = updated)
    }

    fun onPriceRangeChanged(range: ClosedFloatingPointRange<Float>) {
        _uiState.value = _uiState.value.copy(selectedPriceRange = range)
    }

    fun onMinRatingChanged(rating: Float) {
        _uiState.value = _uiState.value.copy(minRating = rating)
    }

    fun onMaxDistanceChanged(distance: Float) {
        _uiState.value = _uiState.value.copy(maxDistanceKm = distance)
    }

    fun onAvailableNowToggled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isAvailableNowOnly = enabled)
    }

    fun resetFilters() {
        _uiState.value = FilterState()
    }
}
