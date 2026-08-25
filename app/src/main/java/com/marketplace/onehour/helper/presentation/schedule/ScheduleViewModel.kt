package com.marketplace.onehour.helper.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.BecomeHelperRequestBody
import com.marketplace.onehour.common.network.CreateAvailabilityRequestBody
import com.marketplace.onehour.common.network.HelperServiceRequestBody
import com.marketplace.onehour.common.network.KycSubmitRequestBody
import com.marketplace.onehour.common.network.ToggleAvailableRequestBody
import com.marketplace.onehour.helper.presentation.onboarding.HelperOnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// day_of_week per migration comment: 0=Sunday..6=Saturday.
private val DAY_OF_WEEK = mapOf(
    "Sun" to 0, "Mon" to 1, "Tue" to 2, "Wed" to 3, "Thu" to 4, "Fri" to 5, "Sat" to 6
)

// Each selected slot label maps to a 24h [start, end) range; multiple
// selected slots for the same day are merged into one continuous window
// since the backend only supports one availability row per day.
private val SLOT_HOURS = mapOf(
    "Morning (8 AM - 12 PM)" to (8 to 12),
    "Afternoon (12 PM - 5 PM)" to (12 to 17),
    "Evening (5 PM - 9 PM)" to (17 to 21)
)

class ScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleState())
    val uiState: StateFlow<ScheduleState> = _uiState.asStateFlow()

    fun onRateChanged(rate: Double) {
        _uiState.value = _uiState.value.copy(hourlyRate = rate)
    }

    fun toggleInstantBooking(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(instantBookingEnabled = enabled)
    }

    fun toggleDay(day: String) {
        val current = _uiState.value.selectedDays
        val updated = if (current.contains(day)) current - day else current + day
        _uiState.value = _uiState.value.copy(selectedDays = updated)
    }

    fun toggleTimeSlot(slot: String) {
        val current = _uiState.value.selectedTimeSlots
        val updated = if (current.contains(slot)) current - slot else current + slot
        _uiState.value = _uiState.value.copy(selectedTimeSlots = updated)
    }

    /**
     * Submits everything collected across all 4 onboarding steps: become-helper
     * profile, KYC document, service/rate, weekly availability, then flips
     * available-now on if the user enabled instant bookings.
     */
    fun completeRegistration(onSuccess: () -> Unit) {
        val categoryId = HelperOnboardingRepository.categoryId
        if (categoryId == null) {
            _uiState.value = _uiState.value.copy(registrationError = "No category selected — go back and pick one.")
            return
        }
        if (_uiState.value.selectedDays.isEmpty() || _uiState.value.selectedTimeSlots.isEmpty()) {
            _uiState.value = _uiState.value.copy(registrationError = "Select at least one working day and time slot.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, registrationError = null)
        viewModelScope.launch {
            try {
                ApiClient.api.becomeHelper(
                    BecomeHelperRequestBody(
                        bio = HelperOnboardingRepository.bio,
                        experienceYears = HelperOnboardingRepository.experienceYears,
                        serviceRadiusKm = 10.0
                    )
                )

                ApiClient.api.submitKyc(
                    KycSubmitRequestBody(
                        documentType = HelperOnboardingRepository.kycDocumentType,
                        documentUrl = HelperOnboardingRepository.kycDocumentUrl
                    )
                )

                ApiClient.api.addHelperService(
                    HelperServiceRequestBody(categoryId = categoryId, hourlyRate = _uiState.value.hourlyRate)
                )

                val hours = _uiState.value.selectedTimeSlots.mapNotNull { SLOT_HOURS[it] }
                val startHour = hours.minOf { it.first }
                val endHour = hours.maxOf { it.second }
                for (day in _uiState.value.selectedDays) {
                    val dayOfWeek = DAY_OF_WEEK[day] ?: continue
                    ApiClient.api.createAvailability(
                        CreateAvailabilityRequestBody(
                            dayOfWeek = dayOfWeek,
                            startTime = "%02d:00:00".format(startHour),
                            endTime = "%02d:00:00".format(endHour)
                        )
                    )
                }

                if (_uiState.value.instantBookingEnabled) {
                    ApiClient.api.setAvailableNow(ToggleAvailableRequestBody(isAvailableNow = true))
                }

                HelperOnboardingRepository.reset()
                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    registrationError = "Registration failed: ${e.message}"
                )
            }
        }
    }
}
