package com.marketplace.onehour.customer.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.LoginRequestBody
import com.marketplace.onehour.common.network.RegisterRequestBody
import com.marketplace.onehour.common.network.TokenStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * The backend has no real OTP delivery/verification endpoint yet — auth is
 * phone+password. The OTP step here stays mocked (matches the original
 * spec: "OTP verification flow, mock OTP for now"), but on success we make a
 * REAL account/login call so every screen after this has a real Sanctum
 * token. The password is a deterministic placeholder derived from the phone
 * number (never shown to the user) until real OTP-based auth replaces this.
 */
private fun placeholderPassword(phone: String) = "onehour-otp-$phone"

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null

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
                otpCode = "123456"
            )
            startResendCooldownTimer()
        }
    }

    fun resendOtp() {
        if (_uiState.value.resendCountdownSeconds > 0) return
        sendOtp()
    }

    private fun startResendCooldownTimer() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(resendCountdownSeconds = 30)
            while (_uiState.value.resendCountdownSeconds > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    resendCountdownSeconds = _uiState.value.resendCountdownSeconds - 1
                )
            }
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        if (_uiState.value.otpCode.length < 4) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid OTP code")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val phone = _uiState.value.phoneNumber
            val password = placeholderPassword(phone)

            val authResponse = try {
                ApiClient.api.login(LoginRequestBody(phone = phone, password = password))
            } catch (loginError: Exception) {
                try {
                    ApiClient.api.register(
                        RegisterRequestBody(
                            name = "Customer $phone",
                            phone = phone,
                            email = null,
                            password = password,
                            role = "customer"
                        )
                    )
                } catch (registerError: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Couldn't sign in: ${registerError.message}"
                    )
                    return@launch
                }
            }

            TokenStore.saveSession(authResponse.token, authResponse.user.role)
            _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
            onSuccess()
        }
    }

    fun resetToPhoneEntry() {
        cooldownJob?.cancel()
        _uiState.value = _uiState.value.copy(isOtpSent = false, otpCode = "", resendCountdownSeconds = 0)
    }
}
