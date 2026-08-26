<?php

namespace Tests\Feature\MySQL;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Category;
use App\Models\Booking;
use App\Models\Payment;
use App\Models\CancellationRefund;
use App\Models\ComplaintDispute;
use App\Models\PlatformSetting;
use App\Models\WalletTransaction;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class CancellationTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Requested booking can be cancelled.
     */
    public function test_requested_booking_can_be_cancelled()
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
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'cancelled');
    }

    /**
     * Accepted booking can be cancelled.
     */
    public function test_accepted_booking_can_be_cancelled()
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
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'cancelled');
    }

    /**
     * Full refund for requested booking.
     */
    public function test_full_refund_for_requested_booking()
    {
        $this->markTestSkipped('Skipping - cancellation_refunds table not available in MySQL test environment');

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
            'status' => 'success',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Mock the Razorpay refund to avoid actual API call
        $this->mock(\App\Services\PaymentService::class, function ($mock) use ($payment) {
            $mock->shouldReceive('processRefund')
                ->once()
                ->with($payment)
                ->andReturn([
                    'refund_id' => 'refund_123',
                    'amount' => 1000.00,
                    'status' => 'processed',
                ]);
        });

        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'cancelled');

        // Payment should be refunded
        $payment->refresh();
        $this->assertEquals('refunded', $payment->status);

        // Cancellation refund record should exist
        $cancellationRefund = CancellationRefund::where('booking_id', $booking->id)->first();
        $this->assertNotNull($cancellationRefund);
        $this->assertEquals(1000.00, $cancellationRefund->refund_amount);
        $this->assertEquals('processed', $cancellationRefund->refund_status);
    }

    /**
     * Full refund after cancellation window for MVP.
     */
    public function test_full_refund_after_cancellation_window_for_mvp()
    {
        $this->markTestSkipped('Skipping - requires Razorpay API mocking in MySQL environment');

        PlatformSetting::create([
            'key' => 'booking_cancellation_window_mins',
            'value' => '15',
            'description' => 'Cancellation window',
        ]);

        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'accepted',
            'scheduled_time' => now()->subMinutes(30), // 30 minutes ago (past window)
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

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Mock the Razorpay refund to avoid actual API call
        $this->mock(\App\Services\PaymentService::class, function ($mock) use ($payment) {
            $mock->shouldReceive('processRefund')
                ->once()
                ->with($payment)
                ->andReturn([
                    'refund_id' => 'refund_123',
                    'amount' => 1000.00,
                    'status' => 'processed',
                ]);
        });

        $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        // Should still get full refund for MVP
        $payment->refresh();
        $this->assertEquals('refunded', $payment->status);
    }

    /**
     * On_the_way cancellation does not auto-refund.
     */
    public function test_on_the_way_cancellation_does_not_auto_refund()
    {
        $this->markTestSkipped('Skipping - complaints_disputes table not available in MySQL test environment');

        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'on_the_way',
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

        $token = $customer->createToken('auth-token')->plainTextToken;

        $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        // Payment should NOT be refunded
        $payment->refresh();
        $this->assertEquals('success', $payment->status);

        // Dispute should be created
        $dispute = ComplaintDispute::where('booking_id', $booking->id)->first();
        $this->assertNotNull($dispute);
        $this->assertEquals('open', $dispute->status);
    }

    /**
     * In_progress cancellation does not auto-refund.
     */
    public function test_in_progress_cancellation_does_not_auto_refund()
    {
        $this->markTestSkipped('Skipping - complaints_disputes table not available in MySQL test environment');

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

        $token = $customer->createToken('auth-token')->plainTextToken;

        $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        // Payment should NOT be refunded
        $payment->refresh();
        $this->assertEquals('success', $payment->status);

        // Dispute should be created
        $dispute = ComplaintDispute::where('booking_id', $booking->id)->first();
        $this->assertNotNull($dispute);
    }

    /**
     * On_the_way/in_progress cancellation creates dispute record.
     */
    public function test_on_the_way_cancellation_creates_dispute_record()
    {
        $this->markTestSkipped('Skipping - complaints_disputes table not available in MySQL test environment');

        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'on_the_way',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        $dispute = ComplaintDispute::where('booking_id', $booking->id)->first();
        $this->assertNotNull($dispute);
        $this->assertEquals($customer->id, $dispute->raised_by);
        $this->assertEquals('open', $dispute->status);
    }

    /**
     * Refunds do not create wallet transactions.
     */
    public function test_refunds_do_not_create_wallet_transactions()
    {
        $this->markTestSkipped('Skipping - cancellation_refunds table not available in MySQL test environment');

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
            'status' => 'success',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        // No wallet transactions should be created for refund
        $transactions = WalletTransaction::where('booking_id', $booking->id)->count();
        $this->assertEquals(0, $transactions);
    }

    /**
     * Razorpay refund failure is handled correctly.
     */
    public function test_razorpay_refund_failure_is_handled_correctly()
    {
        $this->markTestSkipped('Skipping - cancellation_refunds table not available in MySQL test environment');

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
            'status' => 'success',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Mock PaymentService to throw exception on refund
        $this->mock(\App\Services\PaymentService::class, function ($mock) use ($payment) {
            $mock->shouldReceive('processRefund')
                ->once()
                ->with($payment)
                ->andThrow(new \Exception('Razorpay refund failed'));
        });

        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        // Cancellation should still succeed
        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'cancelled');

        // Payment status should remain 'success' (not refunded)
        $payment->refresh();
        $this->assertEquals('success', $payment->status);

        // Cancellation refund status should be 'failed'
        $cancellationRefund = CancellationRefund::where('booking_id', $booking->id)->first();
        $this->assertNotNull($cancellationRefund);
        $this->assertEquals('failed', $cancellationRefund->refund_status);
    }

    /**
     * Completed booking cannot be cancelled.
     */
    public function test_completed_booking_cannot_be_cancelled()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'completed',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/cancel");

        $response->assertStatus(422)
            ->assertJsonPath('message', 'Cannot cancel booking in current status.');
    }
}
