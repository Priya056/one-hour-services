package com.marketplace.onehour.integration.payment

/**
 * Data model mirroring Razorpay's Order API response structure (`/v1/orders`).
 */
data class RazorpayOrderResponse(
    val id: String,
    val entity: String = "order",
    val amountInPaise: Long, // 1 INR = 100 paise
    val amountPaidInPaise: Long = 0,
    val amountDueInPaise: Long,
    val currency: String = "INR",
    val receipt: String,
    val status: String = "created",
    val createdAt: Long = System.currentTimeMillis() / 1000
)

/**
 * Data model mirroring Razorpay's client payment confirmation & webhook payload structure.
 */
data class RazorpayPaymentConfirmResponse(
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String,
    val status: String = "captured", // "captured" or "failed"
    val method: String = "upi",
    val amountInPaise: Long,
    val currency: String = "INR",
    val errorCode: String? = null,
    val errorDescription: String? = null
)

/**
 * Abstract Payment Service contract.
 * Swapping between MockPaymentService and live Razorpay API backend service requires
 * zero changes to booking/UI logic.
 */
interface PaymentService {
    fun createOrder(amountInInr: Double, bookingId: String): RazorpayOrderResponse
    fun confirmPayment(
        orderId: String,
        paymentId: String,
        signature: String
    ): RazorpayPaymentConfirmResponse
}

/**
 * Mock implementation of PaymentService simulating Razorpay order & webhook payloads.
 */
class MockPaymentService : PaymentService {

    override fun createOrder(amountInInr: Double, bookingId: String): RazorpayOrderResponse {
        val amountPaise = (amountInInr * 100).toLong()
        val mockOrderId = "order_mock_${bookingId}_${System.currentTimeMillis()}"
        return RazorpayOrderResponse(
            id = mockOrderId,
            amountInPaise = amountPaise,
            amountDueInPaise = amountPaise,
            currency = "INR",
            receipt = "receipt_$bookingId",
            status = "created"
        )
    }

    override fun confirmPayment(
        orderId: String,
        paymentId: String,
        signature: String
    ): RazorpayPaymentConfirmResponse {
        val mockPaymentId = if (paymentId.isNotBlank()) paymentId else "pay_mock_${System.currentTimeMillis()}"
        val mockSignature = if (signature.isNotBlank()) signature else "sig_mock_${System.currentTimeMillis()}"

        return RazorpayPaymentConfirmResponse(
            razorpayOrderId = orderId,
            razorpayPaymentId = mockPaymentId,
            razorpaySignature = mockSignature,
            status = "captured",
            method = "upi",
            amountInPaise = 3500 // Default 35 INR in paise
        )
    }
}
