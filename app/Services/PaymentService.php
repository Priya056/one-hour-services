<?php

namespace App\Services;

use App\Models\Booking;
use App\Models\Payment;
use App\Models\PlatformSetting;
use Razorpay\Api\Api;
use Illuminate\Support\Facades\DB;

class PaymentService
{
    protected $razorpay;

    public function __construct()
    {
        $keyId = config('services.razorpay.key_id');
        $keySecret = config('services.razorpay.key_secret');
        $this->razorpay = new Api($keyId, $keySecret);
    }

    /**
     * Create Razorpay order for a booking
     */
    public function createOrder(Booking $booking): array
    {
        $razorpayOrder = $this->razorpay->order->create([
            'receipt' => 'booking_' . $booking->id,
            'amount' => $booking->total_amount * 100, // Razorpay expects amount in paise
            'currency' => 'INR',
            'notes' => [
                'booking_id' => $booking->id,
                'customer_id' => $booking->customer_id,
                'helper_id' => $booking->helper_id,
            ],
        ]);

        return [
            'order_id' => $razorpayOrder['id'],
            'amount' => $razorpayOrder['amount'],
            'currency' => $razorpayOrder['currency'],
        ];
    }

    /**
     * Create payment record for a booking
     */
    public function createPayment(Booking $booking, string $gatewayTransactionId): Payment
    {
        return DB::transaction(function () use ($booking, $gatewayTransactionId) {
            $commissionPercent = PlatformSetting::getCommissionPercent();
            $commission = bcmul($booking->total_amount, bcdiv($commissionPercent, 100, 4), 2);
            $helperPayout = bcsub($booking->total_amount, $commission, 2);

            return Payment::create([
                'booking_id' => $booking->id,
                'amount' => $booking->total_amount,
                'platform_commission' => $commission,
                'helper_payout_amount' => $helperPayout,
                'payment_gateway' => 'razorpay',
                'gateway_transaction_id' => $gatewayTransactionId,
                'status' => 'pending',
            ]);
        });
    }

    /**
     * Verify Razorpay webhook signature
     */
    public function verifyWebhookSignature(string $webhookBody, string $webhookSignature, string $webhookSecret): bool
    {
        $expectedSignature = hash_hmac('sha256', $webhookBody, $webhookSecret);
        return hash_equals($expectedSignature, $webhookSignature);
    }

    /**
     * Process payment.captured webhook
     */
    public function processPaymentCaptured(array $webhookData): bool
    {
        $paymentId = $webhookData['payload']['payment']['entity']['id'];
        $orderId = $webhookData['payload']['payment']['entity']['order_id'];
        $amount = $webhookData['payload']['payment']['entity']['amount'] / 100; // Convert from paise

        return DB::transaction(function () use ($paymentId, $orderId, $amount) {
            $payment = Payment::where('gateway_transaction_id', $paymentId)->first();

            if (!$payment) {
                // Payment not found in our system
                return false;
            }

            if ($payment->status === 'success') {
                // Already processed, prevent duplicate
                return true;
            }

            // Update payment status to success
            $payment->update([
                'status' => 'success',
            ]);

            // Booking remains 'requested' - helper can now see it (payment successful)
            // Helper will accept booking separately via updateStatus endpoint

            return true;
        });
    }

    /**
     * Process refund via Razorpay
     */
    public function processRefund(Payment $payment): array
    {
        if ($payment->status !== 'success') {
            throw new \Exception('Payment must be successful to refund');
        }

        if ($payment->status === 'refunded') {
            throw new \Exception('Payment is already refunded');
        }

        // Call Razorpay refund API first
        $razorpayPayment = $this->razorpay->payment->fetch($payment->gateway_transaction_id);
        $refund = $razorpayPayment->refund([
            'amount' => $payment->amount * 100, // Convert to paise
        ]);

        // Update payment status to refunded only after successful Razorpay refund
        $payment->update(['status' => 'refunded']);

        return [
            'refund_id' => $refund['id'],
            'amount' => $refund['amount'] / 100, // Convert from paise
            'status' => $refund['status'],
        ];
    }
}
