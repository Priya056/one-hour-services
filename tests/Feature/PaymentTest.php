<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Category;
use App\Models\Booking;
use App\Models\Payment;
use App\Models\PlatformSetting;
use App\Models\Wallet;
use App\Models\WalletTransaction;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class PaymentTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Payment is associated with booking.
     */
    public function test_payment_is_associated_with_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'pending',
        ]);

        $this->assertEquals($booking->id, $payment->booking_id);
        $this->assertEquals($booking->id, $payment->booking->id);
    }

    /**
     * Commission comes from platform_settings.
     */
    public function test_commission_comes_from_platform_settings()
    {
        PlatformSetting::create([
            'key' => 'default_commission_percent',
            'value' => '20.00',
            'description' => 'Test commission',
        ]);

        $commission = PlatformSetting::getCommissionPercent();
        $this->assertEquals(20.00, $commission);
    }

    /**
     * Helper payout is calculated correctly.
     */
    public function test_helper_payout_is_calculated_correctly()
    {
        $amount = 1000.00;
        $commissionPercent = 15.00;
        $commission = bcmul($amount, bcdiv($commissionPercent, 100, 4), 2);
        $helperPayout = bcsub($amount, $commission, 2);

        $this->assertEquals(150.00, $commission);
        $this->assertEquals(850.00, $helperPayout);
    }

    /**
     * Helper wallet is NOT credited at payment capture.
     */
    public function test_helper_wallet_not_credited_at_payment_capture()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'accepted',
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'success',
        ]);

        // Wallet should not exist or have 0 balance
        $wallet = Wallet::where('helper_id', $helperProfile->id)->first();
        $this->assertNull($wallet);
    }

    /**
     * Helper wallet is credited when booking completes.
     */
    public function test_helper_wallet_credited_when_booking_completes()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'in_progress',
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'success',
        ]);

        // Complete the booking
        $booking->update(['status' => 'completed']);

        // Wallet should be credited
        $wallet = Wallet::where('helper_id', $helperProfile->id)->first();
        $this->assertNotNull($wallet);
        $this->assertEquals(850.00, $wallet->balance);

        // Wallet transaction should exist
        $transaction = WalletTransaction::where('booking_id', $booking->id)
            ->where('type', 'credit')
            ->first();
        $this->assertNotNull($transaction);
        $this->assertEquals(850.00, $transaction->amount);
    }

    /**
     * Duplicate completion does not double-credit wallet.
     */
    public function test_duplicate_completion_does_not_double_credit_wallet()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'in_progress',
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'success',
        ]);

        // Complete the booking first time
        $booking->update(['status' => 'completed']);
        $wallet = Wallet::where('helper_id', $helperProfile->id)->first();
        $balanceAfterFirst = $wallet->balance;

        // Try to complete again (should not double-credit)
        $booking->update(['status' => 'completed']);
        $wallet->refresh();
        $this->assertEquals($balanceAfterFirst, $wallet->balance);

        // Should still have only one credit transaction
        $transactionCount = WalletTransaction::where('booking_id', $booking->id)
            ->where('type', 'credit')
            ->count();
        $this->assertEquals(1, $transactionCount);
    }

    /**
     * Client callback alone cannot set payment status to success.
     */
    public function test_client_callback_alone_cannot_set_payment_success()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'requested',
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'pending',
        ]);

        // Try to update payment status directly via API (should not be possible)
        // The payment status should only change via webhook
        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->putJson("/api/payments/{$payment->id}", [
                'status' => 'success',
            ]);

        // This endpoint doesn't exist, so 405 (Method Not Allowed) is expected
        $response->assertStatus(405);

        // Payment status should still be pending
        $payment->refresh();
        $this->assertEquals('pending', $payment->status);
    }

    /**
     * Unpaid booking is not visible to helper.
     */
    public function test_unpaid_booking_not_visible_to_helper()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        
        // Create booking without successful payment
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'requested',
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'pending', // Not paid yet
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        // Helper should not see unpaid booking
        $response = $this->withToken($token)
            ->getJson('/api/bookings');

        $response->assertStatus(200);
        $this->assertCount(0, $response->json('data'));
    }

    /**
     * Paid booking is visible to helper.
     */
    public function test_paid_booking_visible_to_helper()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        
        // Create booking with successful payment
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'requested',
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'success', // Paid
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        // Helper should see paid booking
        $response = $this->withToken($token)
            ->getJson('/api/bookings');

        $response->assertStatus(200);
        $this->assertCount(1, $response->json('data'));
    }

    /**
     * Invalid webhook signature is rejected.
     */
    public function test_invalid_webhook_signature_is_rejected()
    {
        config(['services.razorpay.webhook_secret' => 'test_secret']);

        $webhookData = [
            'event' => 'payment.captured',
            'payload' => [
                'payment' => [
                    'entity' => [
                        'id' => 'pay_123',
                        'order_id' => 'order_123',
                        'amount' => 100000, // 1000.00 in paise
                    ],
                ],
            ],
        ];

        $response = $this->postJson('/api/webhooks/razorpay', $webhookData, [
            'X-Razorpay-Signature' => 'invalid_signature',
        ]);

        $response->assertStatus(401)
            ->assertJsonPath('message', 'Invalid signature');
    }

    /**
     * Duplicate payment for same booking is prevented.
     */
    public function test_duplicate_payment_for_same_booking_is_prevented()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'total_amount' => 1000.00,
        ]);

        // Create first payment
        Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'pending',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Try to create second payment for same booking
        $response = $this->withToken($token)
            ->postJson('/api/payments', [
                'booking_id' => $booking->id,
                'razorpay_payment_id' => 'pay_456',
            ]);

        $response->assertStatus(422)
            ->assertJsonPath('message', 'Payment already exists for this booking.');
    }

    /**
     * Payment success does not automatically accept booking.
     */
    public function test_payment_success_does_not_automatically_accept_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'requested',
            'total_amount' => 1000.00,
        ]);

        $payment = Payment::create([
            'booking_id' => $booking->id,
            'amount' => 1000.00,
            'platform_commission' => 150.00,
            'helper_payout_amount' => 850.00,
            'payment_gateway' => 'razorpay',
            'gateway_transaction_id' => 'pay_123',
            'status' => 'pending',
        ]);

        // Simulate payment success via webhook
        config(['services.razorpay.webhook_secret' => 'test_secret']);
        $webhookBody = json_encode([
            'event' => 'payment.captured',
            'payload' => [
                'payment' => [
                    'entity' => [
                        'id' => 'pay_123',
                        'order_id' => 'order_123',
                        'amount' => 100000,
                    ],
                ],
            ],
        ]);
        $webhookSignature = hash_hmac('sha256', $webhookBody, 'test_secret');

        $this->postJson('/api/webhooks/razorpay', json_decode($webhookBody, true), [
            'X-Razorpay-Signature' => $webhookSignature,
        ]);

        // Payment should be successful
        $payment->refresh();
        $this->assertEquals('success', $payment->status);

        // Booking should still be 'requested' (not auto-accepted)
        $booking->refresh();
        $this->assertEquals('requested', $booking->status);

        // Helper can now see the booking (because payment is successful)
        $helperToken = $helper->createToken('auth-token')->plainTextToken;
        $response = $this->withToken($helperToken)
            ->getJson('/api/bookings');
        $this->assertCount(1, $response->json('data'));
    }
}
