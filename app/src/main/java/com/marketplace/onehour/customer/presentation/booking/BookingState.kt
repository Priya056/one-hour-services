package com.marketplace.onehour.customer.presentation.booking

import com.marketplace.onehour.common.network.HelperDto

data class BookingState(
    val helper: HelperDto? = null,
    val isInstantBooking: Boolean = true,
    val selectedDate: String = "Today, Aug 24",
    val selectedTimeSlot: String = "Now (Arrive in ~15 mins)",
    val selectedAddress: String = "Home: 42, 4th Cross, Indiranagar, Bengaluru",
    val specialInstructions: String = "",
    val baseHourlyFee: Double = 35.0,
    val platformFee: Double = 3.50,
    val taxAmount: Double = 1.50,
    val totalAmount: Double = 40.0,
    val isLoading: Boolean = false,
    val bookingError: String? = null
)
