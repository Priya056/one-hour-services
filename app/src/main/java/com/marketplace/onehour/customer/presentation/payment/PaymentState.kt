package com.marketplace.onehour.customer.presentation.payment

enum class PaymentMethodType {
    UPI, CARD, NETBANKING, CASH
}

data class PaymentState(
    val bookingId: String = "",
    val totalAmount: Double = 40.0,
    val selectedMethod: PaymentMethodType = PaymentMethodType.UPI,
    val selectedUpiApp: String = "Google Pay",
    val isProcessing: Boolean = false,
    val paymentSuccess: Boolean = false,
    val razorpayPaymentId: String? = null,
    val errorMessage: String? = null
)
