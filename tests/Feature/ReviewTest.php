<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Category;
use App\Models\Booking;
use App\Models\Review;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ReviewTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Customer can create review for their own booking.
     */
    public function test_customer_can_create_review_for_own_booking()
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
            ->postJson('/api/reviews', [
                'booking_id' => $booking->id,
                'helper_id' => $helperProfile->id,
                'rating' => 5,
                'comment' => 'Great service!',
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('data.rating', 5)
            ->assertJsonPath('data.comment', 'Great service!');
    }

    /**
     * Customer cannot review another customer's booking.
     */
    public function test_customer_cannot_review_another_customers_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $otherCustomer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $otherCustomer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/reviews', [
                'booking_id' => $booking->id,
                'helper_id' => $helperProfile->id,
                'rating' => 5,
                'comment' => 'Great service!',
            ]);

        $response->assertStatus(403)
            ->assertJsonPath('message', 'You can only review your own bookings.');
    }

    /**
     * Helper must match the booking.
     */
    public function test_helper_must_match_booking()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $otherHelper = User::factory()->create(['role' => 'helper']);
        $otherHelperProfile = HelperProfile::factory()->create(['user_id' => $otherHelper->id]);
        $category = Category::factory()->create();
        $booking = Booking::factory()->create([
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/reviews', [
                'booking_id' => $booking->id,
                'helper_id' => $otherHelperProfile->id,
                'rating' => 5,
                'comment' => 'Great service!',
            ]);

        $response->assertStatus(422)
            ->assertJsonPath('message', 'The helper must match the booking.');
    }

    /**
     * Rating validation: must be between 1 and 5.
     */
    public function test_rating_must_be_between_1_and_5()
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

        // Test rating 0 (invalid)
        $response = $this->withToken($token)
            ->postJson('/api/reviews', [
                'booking_id' => $booking->id,
                'helper_id' => $helperProfile->id,
                'rating' => 0,
                'comment' => 'Test',
            ]);

        $response->assertStatus(422);

        // Test rating 6 (invalid)
        $response = $this->withToken($token)
            ->postJson('/api/reviews', [
                'booking_id' => $booking->id,
                'helper_id' => $helperProfile->id,
                'rating' => 6,
                'comment' => 'Test',
            ]);

        $response->assertStatus(422);
    }

    /**
     * Comment is optional.
     */
    public function test_comment_is_optional()
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
            ->postJson('/api/reviews', [
                'booking_id' => $booking->id,
                'helper_id' => $helperProfile->id,
                'rating' => 5,
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('data.rating', 5)
            ->assertJsonPath('data.comment', null);
    }

    /**
     * Duplicate review prevention returns clean API error.
     */
    public function test_duplicate_review_prevention_returns_clean_error()
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

        // Create first review
        Review::factory()->create([
            'booking_id' => $booking->id,
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'rating' => 5,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        // Try to create duplicate review
        $response = $this->withToken($token)
            ->postJson('/api/reviews', [
                'booking_id' => $booking->id,
                'helper_id' => $helperProfile->id,
                'rating' => 4,
                'comment' => 'Second review',
            ]);

        $response->assertStatus(422)
            ->assertJsonPath('message', 'A review for this booking already exists.');
    }

    /**
     * Customer can view their own review.
     */
    public function test_customer_can_view_own_review()
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
        $review = Review::factory()->create([
            'booking_id' => $booking->id,
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'rating' => 5,
        ]);

        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson("/api/reviews/{$review->id}");

        $response->assertStatus(200)
            ->assertJsonPath('data.id', $review->id);
    }

    /**
     * Helper can view reviews about them.
     */
    public function test_helper_can_view_reviews_about_them()
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
        $review = Review::factory()->create([
            'booking_id' => $booking->id,
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'rating' => 5,
        ]);

        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson("/api/reviews/{$review->id}");

        $response->assertStatus(200)
            ->assertJsonPath('data.id', $review->id);
    }

    /**
     * Public can view helper's reviews.
     */
    public function test_public_can_view_helper_reviews()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();

        // Create 3 separate bookings with reviews
        for ($i = 0; $i < 3; $i++) {
            $booking = Booking::factory()->create([
                'customer_id' => $customer->id,
                'helper_id' => $helperProfile->id,
                'category_id' => $category->id,
            ]);
            Review::factory()->create([
                'booking_id' => $booking->id,
                'customer_id' => $customer->id,
                'helper_id' => $helperProfile->id,
            ]);
        }

        $response = $this->getJson("/api/helpers/{$helperProfile->id}/reviews");

        $response->assertStatus(200);
        $this->assertCount(3, $response->json('data'));
    }

    /**
     * Unauthorized user cannot view review.
     */
    public function test_unauthorized_user_cannot_view_review()
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
        $review = Review::factory()->create([
            'booking_id' => $booking->id,
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'rating' => 5,
        ]);

        $token = $otherUser->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson("/api/reviews/{$review->id}");

        $response->assertStatus(403);
    }
}
