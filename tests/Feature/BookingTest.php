<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Category;
use App\Models\HelperService;
use App\Models\HelperAvailability;
use App\Models\Booking;
use App\Models\Payment;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class BookingTest extends TestCase
{
    use RefreshDatabase;

    protected function tearDown(): void
    {
        Carbon::setTestNow();
        parent::tearDown();
    }

    /**
     * Customer can create a valid booking.
     */
    public function test_customer_can_create_valid_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'kyc_status' => 'approved',
            'is_available_now' => true,
        ]);
        $category = Category::factory()->create();
        HelperService::factory()->create([
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'hourly_rate' => 500.00,
            'is_active' => true,
        ]);

        // Add availability for the booking time
        HelperAvailability::factory()->create([
            'helper_id' => $helperProfile->id,
            'day_of_week' => 1, // Monday
            'start_time' => '09:00:00',
            'end_time' => '17:00:00',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;
        Carbon::setTestNow(Carbon::create(2024, 1, 1, 10, 0, 0)); // Monday 10 AM

        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::now()->addHours(2)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('data.status', 'requested')
            ->assertJsonPath('data.total_amount', 1000);
    }

    /**
     * Cannot create booking with invalid helper.
     */
    public function test_cannot_create_booking_with_invalid_helper()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $category = Category::factory()->create();

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => 99999,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::now()->addHours(2)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(422);
    }

    /**
     * Cannot create booking with invalid category.
     */
    public function test_cannot_create_booking_with_invalid_category()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'kyc_status' => 'approved',
            'is_available_now' => true,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => 99999,
                'scheduled_time' => Carbon::now()->addHours(2)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(422);
    }

    /**
     * Helper cannot create booking.
     */
    public function test_helper_cannot_create_booking()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::now()->addHours(2)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(403);
    }

    /**
     * Cannot create booking in the past.
     */
    public function test_cannot_create_booking_in_past()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'kyc_status' => 'approved',
            'is_available_now' => true,
        ]);
        $category = Category::factory()->create();
        HelperService::factory()->create([
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'is_active' => true,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::now()->subHours(2)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(422);
    }

    /**
     * Cannot create overlapping booking.
     */
    public function test_cannot_create_overlapping_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'kyc_status' => 'approved',
            'is_available_now' => true,
        ]);
        $category = Category::factory()->create();
        HelperService::factory()->create([
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'hourly_rate' => 500.00,
            'is_active' => true,
        ]);

        HelperAvailability::factory()->create([
            'helper_id' => $helperProfile->id,
            'day_of_week' => 1,
            'start_time' => '09:00:00',
            'end_time' => '17:00:00',
        ]);

        // Create existing booking
        Carbon::setTestNow(Carbon::create(2024, 1, 1, 10, 0, 0));
        Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'scheduled_time' => Carbon::now()->addHours(2),
            'duration_hours' => (float) 2,
            'status' => 'accepted',
            'total_amount' => 1000.00,
            'location_lat' => 12.9716,
            'location_lng' => 77.5946,
            'address_text' => '123 Test Street',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Try to create overlapping booking
        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::now()->addHours(3)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(422);
    }

    /**
     * Cannot create booking for unavailable helper.
     */
    public function test_cannot_create_booking_for_unavailable_helper()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'kyc_status' => 'approved',
            'is_available_now' => false, // Not available
        ]);
        $category = Category::factory()->create();
        HelperService::factory()->create([
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'is_active' => true,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::now()->addHours(2)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(422);
    }

    /**
     * Customer can view own booking.
     */
    public function test_customer_can_view_own_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson("/api/bookings/{$booking->id}");

        $response->assertStatus(200)
            ->assertJsonPath('data.id', $booking->id);
    }

    /**
     * Helper can view assigned booking.
     */
    public function test_helper_can_view_assigned_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson("/api/bookings/{$booking->id}");

        $response->assertStatus(200)
            ->assertJsonPath('data.id', $booking->id);
    }

    /**
     * Unauthorized user cannot view booking.
     */
    public function test_unauthorized_user_cannot_view_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $otherUser = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
        ]);

        $token = $otherUser->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson("/api/bookings/{$booking->id}");

        $response->assertStatus(403);
    }

    /**
     * Valid status transition.
     */
    public function test_valid_status_transition()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $customer = User::factory()->create(['role' => 'customer']);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'requested',
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/status", [
                'status' => 'accepted',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'accepted');
    }

    /**
     * Invalid status transition.
     */
    public function test_invalid_status_transition()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $customer = User::factory()->create(['role' => 'customer']);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'completed',
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/status", [
                'status' => 'in_progress',
            ]);

        $response->assertStatus(422);
    }

    /**
     * Customer can cancel own booking.
     */
    public function test_customer_can_cancel_own_booking()
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
     * Customer can view booking history.
     */
    public function test_customer_can_view_booking_history()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        Booking::factory()->count(3)->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/bookings');

        $response->assertStatus(200);
        $this->assertCount(3, $response->json('data'));
    }

    /**
     * Helper can view booking history.
     */
    public function test_helper_can_view_booking_history()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        
        $bookings = Booking::factory()->count(3)->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
        ]);

        // Add successful payments to bookings so helper can see them
        foreach ($bookings as $booking) {
            Payment::create([
                'booking_id' => $booking->id,
                'amount' => $booking->total_amount,
                'platform_commission' => 150.00,
                'helper_payout_amount' => $booking->total_amount - 150.00,
                'payment_gateway' => 'razorpay',
                'gateway_transaction_id' => 'pay_' . $booking->id,
                'status' => 'success',
            ]);
        }

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/bookings');

        $response->assertStatus(200);
        $this->assertCount(3, $response->json('data'));
    }

    /**
     * Can create booking exactly when another booking ends (back-to-back allowed).
     */
    public function test_can_create_booking_when_another_ends()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'kyc_status' => 'approved',
            'is_available_now' => true,
        ]);
        $category = Category::factory()->create();
        HelperService::factory()->create([
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'hourly_rate' => 500.00,
            'is_active' => true,
        ]);

        HelperAvailability::factory()->create([
            'helper_id' => $helperProfile->id,
            'day_of_week' => 1,
            'start_time' => '09:00:00',
            'end_time' => '17:00:00',
        ]);

        Carbon::setTestNow(Carbon::create(2024, 1, 1, 10, 0, 0));
        
        // Create booking from 12:00 to 14:00
        Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'scheduled_time' => Carbon::create(2024, 1, 1, 12, 0, 0),
            'duration_hours' => (float) 2,
            'status' => 'accepted',
            'total_amount' => 1000.00,
            'location_lat' => 12.9716,
            'location_lng' => 77.5946,
            'address_text' => '123 Test Street',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Try to create booking from 14:00 to 16:00 (exactly when first ends)
        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::create(2024, 1, 1, 14, 0, 0)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(201); // Should succeed - back-to-back allowed
    }

    /**
     * Cancelled booking does not block new booking.
     */
    public function test_cancelled_booking_does_not_block_new_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'kyc_status' => 'approved',
            'is_available_now' => true,
        ]);
        $category = Category::factory()->create();
        HelperService::factory()->create([
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'hourly_rate' => 500.00,
            'is_active' => true,
        ]);

        HelperAvailability::factory()->create([
            'helper_id' => $helperProfile->id,
            'day_of_week' => 1,
            'start_time' => '09:00:00',
            'end_time' => '17:00:00',
        ]);

        Carbon::setTestNow(Carbon::create(2024, 1, 1, 10, 0, 0));
        
        // Create cancelled booking
        Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'scheduled_time' => Carbon::now()->addHours(2),
            'duration_hours' => (float) 2,
            'status' => 'cancelled',
            'total_amount' => 1000.00,
            'location_lat' => 12.9716,
            'location_lng' => 77.5946,
            'address_text' => '123 Test Street',
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Try to create booking in same time slot
        $response = $this->withToken($token)
            ->postJson('/api/bookings', [
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
                'scheduled_time' => Carbon::now()->addHours(2)->toDateTimeString(),
                'duration_hours' => 2,
                'location_lat' => 12.9716,
                'location_lng' => 77.5946,
                'address_text' => '123 Test Street',
            ]);

        $response->assertStatus(201); // Should succeed - cancelled bookings don't block
    }

    /**
     * Invalid status transition through updateStatus is rejected.
     */
    public function test_invalid_status_transition_through_updateStatus_is_rejected()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'completed',
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        // Try to transition from completed to accepted (invalid)
        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/status", [
                'status' => 'accepted',
            ]);

        $response->assertStatus(422)
            ->assertJsonPath('message', 'Cannot transition from completed to accepted.');
    }

    /**
     * Valid status transition through updateStatus works.
     */
    public function test_valid_status_transition_through_updateStatus_works()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'status' => 'requested',
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        // Valid transition: requested -> accepted
        $response = $this->withToken($token)
            ->patchJson("/api/bookings/{$booking->id}/status", [
                'status' => 'accepted',
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('data.status', 'accepted');
    }
}
