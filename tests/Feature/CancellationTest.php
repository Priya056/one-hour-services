<?php

namespace Tests\Feature;

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
     * Refunds do not create wallet transactions.
     */
    public function test_refunds_do_not_create_wallet_transactions()
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
