<?php

namespace Tests\Feature\MySQL;

use App\Models\User;
use App\Models\HelperProfile;
use App\Models\HelperAvailability;
use App\Services\AvailabilityService;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AvailabilityTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Test helper can create availability schedule.
     */
    public function test_helper_can_create_availability_schedule()
    {
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
            ->assertJsonPath('data.day_of_week', 1)
            ->assertJsonPath('data.day_name', 'Monday');
    }

    /**
     * Test time-based availability check.
     */
    public function test_time_based_availability_check()
    {
        $helper = User::factory()->create(['role' => 'helper', 'is_active' => true]);
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
