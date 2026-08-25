package com.marketplace.onehour.helper.presentation.activejob

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class JobLifecycleStatus {
    ACCEPTED,
    ON_THE_WAY,
    ARRIVED,
    IN_PROGRESS,
    COMPLETED
}

data class ActiveJobState(
    val bookingId: String = "BK-8842",
    val customerName: String = "Priya Sharma",
    val customerPhone: String = "+91 98765 43210",
    val address: String = "42, 10th Main Rd, Indiranagar, Bengaluru",
    val serviceCategory: String = "Electrical Repair",
    val totalAmount: String = "₹499.00",
    val otpCodeInput: String = "",
    val status: JobLifecycleStatus = JobLifecycleStatus.ACCEPTED,
    val otpError: String? = null
)

class ActiveJobViewModel : ViewModel() {
    private val _state = MutableStateFlow(ActiveJobState())
    val state: StateFlow<ActiveJobState> = _state.asStateFlow()

    fun updateStatus(newStatus: JobLifecycleStatus) {
        _state.update { it.copy(status = newStatus, otpError = null) }
    }

    fun setOtpInput(input: String) {
        _state.update { it.copy(otpCodeInput = input) }
    }

    fun verifyOtpAndStart(validOtp: String = "1234") {
        if (_state.value.otpCodeInput == validOtp) {
            _state.update { it.copy(status = JobLifecycleStatus.IN_PROGRESS, otpError = null) }
        } else {
            _state.update { it.copy(otpError = "Invalid OTP entered. Ask customer for 4-digit code.") }
        }
    }
}
