package com.marketplace.onehour.customer.presentation.auth

data class AuthState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val resendCountdownSeconds: Int = 30,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)
