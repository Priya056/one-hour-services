<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreatePaymentRequest;
use App\Http\Resources\PaymentResource;
use App\Models\Booking;
use App\Models\Payment;
use App\Services\PaymentService;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class PaymentController extends Controller
{
    use AuthorizesRequests;

    protected $paymentService;

    public function __construct(PaymentService $paymentService)
    {
        $this->paymentService = $paymentService;
    }

    /**
     * Create Razorpay order for a booking
     */
    public function createOrder(Request $request)
    {
        $request->validate([
            'booking_id' => ['required', 'exists:bookings,id'],
        ]);

        $booking = Booking::findOrFail($request->booking_id);
        $this->authorize('view', $booking);

        // Check if payment already exists
        if ($booking->payment) {
            return response()->json([
                'message' => 'Payment already exists for this booking.',
            ], 422);
        }

        // Create Razorpay order
        $order = $this->paymentService->createOrder($booking);

        return response()->json([
            'order_id' => $order['order_id'],
            'amount' => $booking->total_amount,
            'currency' => $order['currency'],
        ]);
    }

    /**
     * Create payment record after Razorpay payment initiation
     */
    public function store(CreatePaymentRequest $request)
    {
        $booking = Booking::findOrFail($request->booking_id);
        $this->authorize('view', $booking);

        // Check if payment already exists
        if ($booking->payment) {
            return response()->json([
                'message' => 'Payment already exists for this booking.',
            ], 422);
        }

        // Create payment record
        $payment = $this->paymentService->createPayment($booking, $request->razorpay_payment_id);

        return new PaymentResource($payment);
    }

    /**
     * Get payment details
     */
    public function show(Request $request, $id)
    {
        $payment = Payment::with(['booking', 'booking.customer', 'booking.helper'])->findOrFail($id);
        $this->authorize('view', $payment->booking);

        return new PaymentResource($payment);
    }
}
