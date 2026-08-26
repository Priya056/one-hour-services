package com.marketplace.onehour.customer.presentation.history

import com.marketplace.onehour.common.network.BookingDto

data class HistoryState(
    val selectedTabIndex: Int = 0,
    val upcomingBookings: List<BookingDto> = emptyList(),
    val pastBookings: List<BookingDto> = emptyList(),
    val isLoading: Boolean = false
)
