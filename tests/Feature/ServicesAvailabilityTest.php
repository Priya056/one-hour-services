<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Category;
use App\Models\HelperService;
use App\Models\HelperAvailability;
use App\Services\AvailabilityService;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ServicesAvailabilityTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Test category listing.
     */
    public function test_category_listing()
    {
        Category::factory()->count(3)->create();

        $response = $this->getJson('/api/categories');

        $response->assertStatus(200);
    }

    /**
     * Test category details.
     */
    public function test_category_details()
    {
        $category = Category::factory()->create();

        $response = $this->getJson("/api/categories/{$category->id}");

        $response->assertStatus(200)
            ->assertJsonPath('data.id', $category->id)
            ->assertJsonPath('data.name', $category->name);
    }

    /**
     * Test helper can create service mapping.
     */
    public function test_helper_can_create_service_mapping()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/helper/services', [
                'category_id' => $category->id,
                'hourly_rate' => 500.00,
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('data.hourly_rate', '500.00')
            ->assertJsonPath('data.is_active', true);
    }

    /**
     * Test helper can update hourly rate.
     */
    public function test_helper_can_update_hourly_rate()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();
        $service = HelperService::factory()->create([
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'hourly_rate' => 500.00,
        ]);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->putJson("/api/helper/services/{$service->id}", [
                'hourly_rate' => 600.00,
            ]);

        $response->assertStatus(200)
            ->assertJsonPath('data.hourly_rate', '600.00');
    }

    /**
     * Test helper can create availability schedule.
     */
    public function test_helper_can_create_availability_schedule()
    {
        $this->markTestSkipped('Skipping availability test - requires MySQL database integration');

        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/helper/availability', [
                'day_of_week' => 1, // Monday
                'start_time' => '09:00',
                'end_time' => '17:00',
            ]);

        $response->assertStatus(201)
            ->assertJson([
                'day_of_week' => 1,
                'day_name' => 'Monday',
            ]);
    }

    /**
     * Test helper can toggle available-now status.
     */
    public function test_helper_can_toggle_available_now_status()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'is_available_now' => false,
        ]);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson('/api/helper/available-now', [
                'is_available_now' => true,
            ]);

        $response->assertStatus(200)
            ->assertJson(['is_available_now' => true]);
    }

    /**
     * Test unavailable helper cannot be booked.
     */
    public function test_unavailable_helper_cannot_be_booked()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'is_available_now' => false,
            'kyc_status' => 'approved',
        ]);

        $availabilityService = new AvailabilityService();
        $isAvailable = $availabilityService->isAvailableNow($helperProfile);

        $this->assertFalse($isAvailable);
    }

    /**
     * Test time-based availability check.
     */
    public function test_time_based_availability_check()
    {
        $this->markTestSkipped('Skipping availability test - requires MySQL database integration');

        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create([
            'user_id' => $helper->id,
            'is_available_now' => true,
            'kyc_status' => 'approved',
        ]);

        // Create availability for Monday (day 1) from 9 AM to 5 PM
        HelperAvailability::factory()->create([
            'helper_id' => $helperProfile->id,
            'day_of_week' => 1,
            'start_time' => '09:00:00',
            'end_time' => '17:00:00',
        ]);

        $availabilityService = new AvailabilityService();

        // Test Monday at 10 AM (should be available)
        $mondayMorning = Carbon::create(2024, 1, 1, 10, 0, 0); // Monday
        $isAvailable = $availabilityService->isAvailableAt($helperProfile, $mondayMorning);
        $this->assertTrue($isAvailable);

        // Test Monday at 8 AM (should not be available)
        $mondayEarly = Carbon::create(2024, 1, 1, 8, 0, 0); // Monday
        $isAvailable = $availabilityService->isAvailableAt($helperProfile, $mondayEarly);
        $this->assertFalse($isAvailable);
    }
}
