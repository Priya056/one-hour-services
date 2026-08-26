package com.marketplace.onehour.customer.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.placeholders.RazorpayPlaceholder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentState())
    val uiState: StateFlow<PaymentState> = _uiState.asStateFlow()

    fun setBookingDetails(bookingId: String, amount: Double = 40.0) {
        _uiState.value = _uiState.value.copy(
            bookingId = bookingId,
            totalAmount = amount
        )
    }

    fun selectPaymentMethod(method: PaymentMethodType) {
        _uiState.value = _uiState.value.copy(selectedMethod = method)
    }

    fun selectUpiApp(app: String) {
        _uiState.value = _uiState.value.copy(selectedUpiApp = app)
    }

    fun processPayment(
        activity: android.app.Activity? = null,
        onSuccess: (bookingId: String) -> Unit
    ) {
        _uiState.value = _uiState.value.copy(isProcessing = true)

        viewModelScope.launch {
            // Initiate Razorpay Test Checkout via RazorpayPlaceholder / PaymentHelper
            // TODO: In production, connect this to backend POST /api/payments/initiate endpoint first.
            RazorpayPlaceholder.initiatePayment(
                activity = activity,
                amountInInr = _uiState.value.totalAmount,
                bookingId = _uiState.value.bookingId,
                customerEmail = "customer@lumina.com",
                customerPhone = "9876543210",
                onSuccess = { paymentId ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        paymentSuccess = true,
                        razorpayPaymentId = paymentId
                    )
                    onSuccess(_uiState.value.bookingId)
                },
                onFailure = { code, desc ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        errorMessage = "Payment failed: $desc ($code)"
                    )
                }
            )
        }
    }
}
