package com.marketplace.onehour.integration.payment

import android.app.Activity
import android.util.Log
import com.marketplace.onehour.BuildConfig
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

/**
 * Modular Razorpay Payment Helper.
 * Evaluates BuildConfig.PAYMENT_MODE ("mock" vs "live_test").
 * Dispatches to MockPaymentService in mock mode or Razorpay Checkout SDK in live_test mode.
 */
class PaymentHelper : PaymentResultWithDataListener {

    private var onSuccessCallback: ((paymentId: String) -> Unit)? = null
    private var onFailureCallback: ((errorCode: Int, description: String) -> Unit)? = null
    private val mockPaymentService = MockPaymentService()

    /**
     * Initializes and launches payment flow (Mock or Razorpay Test Checkout).
     *
     * TODO: In production, connect this to backend POST /api/payments/initiate to fetch a signed order_id first.
     */
    fun startPayment(
        activity: Activity?,
        amountInInr: Double,
        bookingId: String,
        customerEmail: String = "customer@lumina.com",
        customerPhone: String = "9876543210",
        onSuccess: (paymentId: String) -> Unit,
        onFailure: (errorCode: Int, description: String) -> Unit
    ) {
        this.onSuccessCallback = onSuccess
        this.onFailureCallback = onFailure

        val isMockMode = BuildConfig.PAYMENT_MODE.equals("mock", ignoreCase = true) || activity == null

        if (isMockMode) {
            Log.d("PaymentHelper", "PAYMENT_MODE=mock active. Executing MockPaymentService.")
            val mockOrder = mockPaymentService.createOrder(amountInInr, bookingId)
            val mockConfirm = mockPaymentService.confirmPayment(
                orderId = mockOrder.id,
                paymentId = "pay_mock_${System.currentTimeMillis()}",
                signature = "sig_mock_${System.currentTimeMillis()}"
            )
            onSuccess(mockConfirm.razorpayPaymentId)
            return
        }

        // Live Test Mode with Razorpay SDK
        Log.d("PaymentHelper", "PAYMENT_MODE=live_test active. Opening Razorpay Checkout SDK.")
        Checkout.preload(activity.applicationContext)
        val checkout = Checkout()
        val keyId = BuildConfig.RAZORPAY_KEY_ID.ifBlank { "rzp_test_placeholderKey123" }
        checkout.setKeyID(keyId)

        try {
            val options = JSONObject().apply {
                put("name", "Lumina 1-Hour Marketplace")
                put("description", "1-Hour Service Payment (Booking #$bookingId)")
                put("theme.color", "#2563EB")
                put("currency", "INR")
                // Amount in paise (1 INR = 100 paise)
                put("amount", (amountInInr * 100).toLong())

                val prefill = JSONObject().apply {
                    put("email", customerEmail)
                    put("contact", customerPhone)
                }
                put("prefill", prefill)

                val retryObj = JSONObject().apply {
                    put("enabled", true)
                    put("max_count", 2)
                }
                put("retry", retryObj)
            }

            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("PaymentHelper", "Error initiating Razorpay checkout", e)
            onFailure(Checkout.INVALID_OPTIONS, e.localizedMessage ?: "Failed to open Razorpay checkout")
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Log.d("PaymentHelper", "Razorpay Payment Successful. ID: $razorpayPaymentId")
        val paymentId = razorpayPaymentId ?: paymentData?.paymentId ?: "pay_success_mock"
        onSuccessCallback?.invoke(paymentId)
    }

    override fun onPaymentError(errorCode: Int, responseDescription: String?, paymentData: PaymentData?) {
        Log.e("PaymentHelper", "Razorpay Payment Failed ($errorCode): $responseDescription")
        onFailureCallback?.invoke(errorCode, responseDescription ?: "Payment failed or cancelled by user")
    }
}
