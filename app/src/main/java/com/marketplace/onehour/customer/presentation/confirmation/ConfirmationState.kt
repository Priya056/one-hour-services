package com.marketplace.onehour.customer.presentation.confirmation

import com.marketplace.onehour.common.network.HelperDto

data class ConfirmationState(
    val bookingId: String = "b101",
    val bookingReferenceCode: String = "#1H-894201",
    val helper: HelperDto? = null,
    val estimatedEta: String = "15 Mins (Arriving by 02:15 PM)",
    val totalPaid: Double = 40.0,
    val serviceAddress: String = "Home: 42, 4th Cross, Indiranagar, Bengaluru",
    val isLoading: Boolean = false
)
