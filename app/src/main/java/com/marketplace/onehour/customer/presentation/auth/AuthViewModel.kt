package com.marketplace.onehour.customer.presentation.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.FirebaseLoginRequestBody
import com.marketplace.onehour.common.network.TokenStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * Real SMS OTP via Firebase Phone Auth: the SDK sends and validates the
 * code itself (no code ever touches our backend), and on success we hand
 * the resulting Firebase ID token to /api/auth/firebase-login, which
 * verifies it server-side and issues our own Sanctum token.
 */
class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var loginSuccessCallback: (() -> Unit)? = null

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

    fun sendOtp(activity: Activity, onSuccess: () -> Unit) {
        if (_uiState.value.phoneNumber.length < 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number")
            return
        }
        loginSuccessCallback = onSuccess
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Instant auto-verification (SMS auto-retrieval or a
                // previously-trusted device) — no code entry needed at all.
                signInWithCredential(credential)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Couldn't send verification code."
                )
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = verificationId
                resendToken = token
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isOtpSent = true,
                    otpCode = ""
                )
                startResendCooldownTimer()
            }
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+91${_uiState.value.phoneNumber}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
        resendToken?.let { optionsBuilder.setForceResendingToken(it) }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    fun resendOtp(activity: Activity) {
        if (_uiState.value.resendCountdownSeconds > 0) return
        val onSuccess = loginSuccessCallback ?: return
        sendOtp(activity, onSuccess)
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

    fun verifyOtp() {
        val verificationId = storedVerificationId
        if (verificationId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Request a new code and try again.")
            return
        }
        if (_uiState.value.otpCode.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter the 6-digit code")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        signInWithCredential(PhoneAuthProvider.getCredential(verificationId, _uiState.value.otpCode))
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                val result = FirebaseAuth.getInstance().signInWithCredential(credential).await()
                val idToken = result.user?.getIdToken(false)?.await()?.token
                    ?: error("No ID token from Firebase.")

                val authResponse = ApiClient.api.firebaseLogin(FirebaseLoginRequestBody(idToken = idToken))

                TokenStore.saveSession(
                    token = authResponse.token,
                    role = authResponse.user.role,
                    name = authResponse.user.name,
                    phone = authResponse.user.phone,
                    email = authResponse.user.email
                )
                _uiState.value = _uiState.value.copy(isLoading = false, isAuthenticated = true)
                loginSuccessCallback?.invoke()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "That code didn't work — ${e.message ?: "please try again."}"
                )
            } finally {
                // The app's own Sanctum token is the real session from here;
                // Firebase Auth only existed to prove phone ownership.
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    fun resetToPhoneEntry() {
        cooldownJob?.cancel()
        storedVerificationId = null
        resendToken = null
        _uiState.value = _uiState.value.copy(isOtpSent = false, otpCode = "", resendCountdownSeconds = 0)
    }
}
