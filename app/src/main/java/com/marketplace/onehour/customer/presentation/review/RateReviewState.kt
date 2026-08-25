package com.marketplace.onehour.customer.presentation.review

import com.marketplace.onehour.common.network.HelperDto

data class RateReviewState(
    val bookingId: String = "b101",
    val helper: HelperDto? = null,
    val selectedRating: Int = 5,
    val selectedTags: Set<String> = setOf("Punctual & On Time", "Professional Work"),
    val commentText: String = "",
    val selectedTipAmount: Double = 5.0,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false
)
