package com.marketplace.onehour.customer.presentation.profile

import com.marketplace.onehour.common.network.HelperDto

data class ReviewItem(
    val id: String,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class HelperProfileState(
    val helper: HelperDto? = null,
    val reviews: List<ReviewItem> = emptyList(),
    val isLoading: Boolean = true
)
