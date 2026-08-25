package com.marketplace.onehour.customer.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun onPhoneNumberChanged(number: String) {
        if (number.length <= 10 && number.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(phoneNumber = number, errorMessage = null)
        }
    }

    fun onOtpChanged(otp: String) {
        if (otp.length <= 6 && otp.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(otpCode = otp, errorMessage = null)
        }
    }

    fun sendOtp() {
        if (_uiState.value.phoneNumber.length < 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            delay(1000)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isOtpSent = true,
                otpCode = "123456" // Pre-fill mock OTP for quick testing
            )
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        if (_uiState.value.otpCode.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid OTP code")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            delay(1200)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = true
            )
            onSuccess()
        }
    }

    fun resetToPhoneEntry() {
        _uiState.value = _uiState.value.copy(isOtpSent = false, otpCode = "")
    }
}
