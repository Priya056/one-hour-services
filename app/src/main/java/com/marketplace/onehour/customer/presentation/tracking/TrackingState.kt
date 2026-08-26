package com.marketplace.onehour.customer.presentation.tracking

import com.marketplace.onehour.common.network.HelperDto

enum class BookingStatus(val title: String, val index: Int) {
    REQUESTED("Requested", 0),
    ACCEPTED("Accepted", 1),
    ON_THE_WAY("On the way", 2),
    IN_PROGRESS("In Progress", 3),
    COMPLETED("Completed", 4)
}

data class TrackingState(
    val bookingId: String = "b101",
    val status: BookingStatus = BookingStatus.ON_THE_WAY,
    val helper: HelperDto? = null,
    val estimatedArrivalMins: Int = 12,
    val serviceTimeRemainingSeconds: Int = 3600,
    val steps: List<String> = listOf("Requested", "Accepted", "On the way", "In Progress", "Completed"),
    val isLoading: Boolean = false
)
