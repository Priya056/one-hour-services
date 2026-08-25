package com.marketplace.onehour.customer.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.MockDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RateReviewViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RateReviewState())
    val uiState: StateFlow<RateReviewState> = _uiState.asStateFlow()

    fun loadReviewDetails(bookingId: String) {
        viewModelScope.launch {
            val helper = MockDataProvider.sampleHelpers.first()
            _uiState.value = _uiState.value.copy(
                bookingId = bookingId,
                helper = helper
            )
        }
    }

    fun onRatingChanged(rating: Int) {
        _uiState.value = _uiState.value.copy(selectedRating = rating)
    }

    fun toggleTag(tag: String) {
        val current = _uiState.value.selectedTags
        val updated = if (current.contains(tag)) current - tag else current + tag
        _uiState.value = _uiState.value.copy(selectedTags = updated)
    }

    fun onCommentChanged(comment: String) {
        _uiState.value = _uiState.value.copy(commentText = comment)
    }

    fun selectTip(tip: Double) {
        _uiState.value = _uiState.value.copy(selectedTipAmount = tip)
    }

    fun submitReview(onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(isSubmitting = true)
        viewModelScope.launch {
            delay(1000)
            _uiState.value = _uiState.value.copy(isSubmitting = false, isSubmitted = true)
            onSuccess()
        }
    }
}
