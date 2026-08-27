package com.marketplace.onehour.customer.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.CreateReviewRequestBody
import com.marketplace.onehour.common.network.resolveHelperDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RateReviewViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RateReviewState())
    val uiState: StateFlow<RateReviewState> = _uiState.asStateFlow()

    private var helperIdForReview: Int? = null

    fun loadReviewDetails(bookingId: String) {
        val bookingIdInt = bookingId.toIntOrNull()
        if (bookingIdInt == null) {
            _uiState.value = _uiState.value.copy(bookingId = bookingId)
            return
        }

        viewModelScope.launch {
            try {
                val booking = ApiClient.api.getBooking(bookingIdInt).data
                helperIdForReview = booking.helperId
                _uiState.value = _uiState.value.copy(
                    bookingId = bookingId,
                    helper = booking.resolveHelperDisplay()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(bookingId = bookingId)
            }
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

    // No backend support for tips yet (payments/wallet schema has no tip
    // concept) — stays a UI-only selection until that's designed.
    fun selectTip(tip: Double) {
        _uiState.value = _uiState.value.copy(selectedTipAmount = tip)
    }

    fun submitReview(onSuccess: () -> Unit) {
        val bookingIdInt = _uiState.value.bookingId.toIntOrNull()
        val helperId = helperIdForReview

        if (bookingIdInt == null || helperId == null) {
            _uiState.value = _uiState.value.copy(submitError = "Missing booking details — go back and try again.")
            return
        }

        _uiState.value = _uiState.value.copy(isSubmitting = true, submitError = null)
        viewModelScope.launch {
            try {
                ApiClient.api.createReview(
                    CreateReviewRequestBody(
                        bookingId = bookingIdInt,
                        helperId = helperId,
                        rating = _uiState.value.selectedRating,
                        comment = _uiState.value.commentText.ifBlank { null }
                    )
                )
                _uiState.value = _uiState.value.copy(isSubmitting = false, isSubmitted = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitError = "Couldn't submit review: ${e.message}"
                )
            }
        }
    }
}
