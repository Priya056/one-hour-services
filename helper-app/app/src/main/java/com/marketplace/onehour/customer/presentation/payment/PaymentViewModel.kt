package com.marketplace.onehour.customer.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.placeholders.RazorpayPlaceholder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentState())
    val uiState: StateFlow<PaymentState> = _uiState.asStateFlow()

    /**
     * Fetches the real booking to get its actual total_amount — this used to
     * default to a hardcoded $40 regardless of the helper's real rate.
     */
    fun setBookingDetails(bookingId: String) {
        _uiState.value = _uiState.value.copy(bookingId = bookingId)
        val bookingIdInt = bookingId.toIntOrNull() ?: return
        viewModelScope.launch {
            try {
                val booking = ApiClient.api.getBooking(bookingIdInt).data
                _uiState.value = _uiState.value.copy(totalAmount = booking.totalAmount)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Couldn't load booking amount: ${e.message}")
            }
        }
    }

    fun selectPaymentMethod(method: PaymentMethodType) {
        _uiState.value = _uiState.value.copy(selectedMethod = method)
    }

    fun selectUpiApp(app: String) {
        _uiState.value = _uiState.value.copy(selectedUpiApp = app)
    }

    fun processPayment(onSuccess: (bookingId: String) -> Unit) {
        _uiState.value = _uiState.value.copy(isProcessing = true)

        viewModelScope.launch {
            delay(1500) // Simulate payment gateway processing time

            // TODO: In production, invoke Razorpay Checkout activity here using RazorpayPlaceholder
            RazorpayPlaceholder.initiatePayment(
                amountInCents = (_uiState.value.totalAmount * 100).toLong(),
                orderId = "order_${_uiState.value.bookingId}",
                customerEmail = "customer@example.com",
                customerPhone = "+919876543210",
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
