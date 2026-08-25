package com.marketplace.onehour.helper.presentation.schedule

data class ScheduleState(
    val hourlyRate: Double = 35.0,
    val instantBookingEnabled: Boolean = true,
    val selectedDays: Set<String> = setOf("Mon", "Tue", "Wed", "Thu", "Fri"),
    val selectedTimeSlots: Set<String> = setOf("Morning (8 AM - 12 PM)", "Afternoon (12 PM - 5 PM)"),
    val isComplete: Boolean = false,
    val isLoading: Boolean = false,
    val registrationError: String? = null
)
