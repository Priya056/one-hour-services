package com.marketplace.onehour.customer.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.FirebaseLoginRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private var cooldownJob: Job? = null
    private val auth = FirebaseAuth.getInstance()
    private var verificationId: String? = null
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

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
        
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        val phoneNumber = "+91${_uiState.value.phoneNumber}"
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(null) // Will need activity for reCAPTCHA
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification (rare on Android)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to send OTP: ${e.message}"
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@AuthViewModel.verificationId = verificationId
                    this@AuthViewModel.forceResendingToken = token
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpSent = true
                    )
                    startResendCooldownTimer()
                }
            })
            .build()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(options)
    }

    fun resendOtp() {
        if (_uiState.value.resendCountdownSeconds > 0) return
        
        if (_uiState.value.phoneNumber.length < 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number")
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        val phoneNumber = "+91${_uiState.value.phoneNumber}"
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(null)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to resend OTP: ${e.message}"
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@AuthViewModel.verificationId = verificationId
                    this@AuthViewModel.forceResendingToken = token
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpSent = true,
                        errorMessage = null
                    )
                    startResendCooldownTimer()
                }
            })
            .setForceResendingToken(forceResendingToken)
            .build()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(options)
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        val otp = _uiState.value.otpCode
        val vid = verificationId
        
        if (otp.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 6-digit OTP code")
            return
        }
        
        if (vid == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Session expired. Please request a new OTP.")
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        val credential = PhoneAuthProvider.getCredential(vid, otp)
        signInWithPhoneAuthCredential(credential, onSuccess)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, onSuccess: (() -> Unit)? = null) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase auth successful, now get ID token and exchange for Sanctum token
                    val user = task.result?.user
                    user?.getIdToken(true)?.addOnSuccessListener { result ->
                        val firebaseIdToken = result.token
                        exchangeFirebaseTokenForSanctumToken(firebaseIdToken, onSuccess)
                    }?.addOnFailureListener { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Failed to get Firebase ID token: ${e.message}"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Invalid OTP code"
                    )
                }
            }
    }

    private fun exchangeFirebaseTokenForSanctumToken(firebaseIdToken: String, onSuccess: (() -> Unit)?) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.firebaseLogin(
                    FirebaseLoginRequest(id_token = firebaseIdToken)
                )
                
                // Store the Sanctum token for API calls
                ApiClient.setAuthToken(response.token)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    errorMessage = null
                )
                
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Authentication failed: ${e.message}"
                )
            }
        }
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

    fun resetToPhoneEntry() {
        cooldownJob?.cancel()
        verificationId = null
        forceResendingToken = null
        _uiState.value = _uiState.value.copy(
            isOtpSent = false, 
            otpCode = "", 
            resendCountdownSeconds = 0,
            errorMessage = null
        )
    }

    fun logout() {
        auth.signOut()
        ApiClient.clearAuthToken()
        _uiState.value = AuthState()
    }
}
