package com.marketplace.onehour.common.placeholders

import android.app.Activity
import com.marketplace.onehour.integration.payment.PaymentHelper

/**
 * Razorpay Payment Gateway integration point.
 * Delegates payment initiation to the modular PaymentHelper.
 */
object RazorpayPlaceholder {
    fun initiatePayment(
        activity: Activity? = null,
        amountInInr: Double = 40.0,
        bookingId: String = "b101",
        customerEmail: String = "customer@lumina.com",
        customerPhone: String = "9876543210",
        onSuccess: (paymentId: String) -> Unit,
        onFailure: (errorCode: Int, description: String) -> Unit
    ) {
        if (activity != null) {
            val paymentHelper = PaymentHelper()
            paymentHelper.startPayment(
                activity = activity,
                amountInInr = amountInInr,
                bookingId = bookingId,
                customerEmail = customerEmail,
                customerPhone = customerPhone,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        } else {
            // Fallback mock payment response when activity context is not available
            val mockPaymentId = "pay_mock_${System.currentTimeMillis()}"
            onSuccess(mockPaymentId)
        }
    }
}
