<?php

namespace App\Http\Controllers;

use App\Services\PaymentService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class WebhookController extends Controller
{
    protected $paymentService;

    public function __construct(PaymentService $paymentService)
    {
        $this->paymentService = $paymentService;
    }

    /**
     * Handle Razorpay webhook
     */
    public function razorpay(Request $request)
    {
        $webhookSecret = config('services.razorpay.webhook_secret');
        $webhookSignature = $request->header('X-Razorpay-Signature');
        $webhookBody = $request->getContent();

        // Verify webhook signature
        if (!$this->paymentService->verifyWebhookSignature($webhookBody, $webhookSignature, $webhookSecret)) {
            Log::warning('Invalid Razorpay webhook signature');
            return response()->json(['message' => 'Invalid signature'], 401);
        }

        $webhookData = $request->json()->all();
        $event = $webhookData['event'] ?? null;

        // Process different webhook events
        switch ($event) {
            case 'payment.captured':
                $success = $this->paymentService->processPaymentCaptured($webhookData);
                if ($success) {
                    return response()->json(['message' => 'Payment captured successfully']);
                }
                return response()->json(['message' => 'Payment not found or already processed'], 404);

            case 'payment.failed':
                // Handle payment failure
                Log::info('Payment failed webhook received', ['data' => $webhookData]);
                return response()->json(['message' => 'Payment failure logged']);

            default:
                Log::info('Unhandled Razorpay webhook event', ['event' => $event]);
                return response()->json(['message' => 'Event acknowledged']);
        }
    }
}
