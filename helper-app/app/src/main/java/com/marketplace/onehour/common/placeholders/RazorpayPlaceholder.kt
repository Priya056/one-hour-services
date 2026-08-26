package com.marketplace.onehour.common.placeholders

/**
 * Razorpay Payment Gateway integration contract placeholder.
 * TODO: Initialize com.razorpay.Checkout and attach PaymentResultWithDataListener in production.
 */
object RazorpayPlaceholder {
    fun initiatePayment(
        amountInCents: Long,
        orderId: String,
        customerEmail: String,
        customerPhone: String,
        onSuccess: (paymentId: String) -> Unit,
        onFailure: (errorCode: Int, description: String) -> Unit
    ) {
        // Mock payment response for frontend development
        val mockPaymentId = "pay_mock_${System.currentTimeMillis()}"
        onSuccess(mockPaymentId)
    }
}
