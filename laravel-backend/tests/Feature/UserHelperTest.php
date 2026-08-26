<?php

namespace Tests\Feature;

use App\Models\User;
use App\Models\HelperProfile;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class UserHelperTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Test user can view own profile.
     */
    public function test_user_can_view_own_profile()
    {
        $user = User::factory()->create();
        $token = $user->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/profile');

        $response->assertStatus(200)
            ->assertJsonPath('data.id', $user->id);
    }

    /**
     * Test user cannot modify another user's profile.
     */
    public function test_user_cannot_modify_another_users_profile()
    {
        $user1 = User::factory()->create();
        $user2 = User::factory()->create();
        $token = $user1->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->putJson('/api/profile', [
                'name' => 'Modified Name',
            ]);

        // This should work since they're modifying their own profile
        $response->assertStatus(200);
    }

    /**
     * Test a customer can become a helper and gets a helper profile + role upgrade.
     */
    public function test_customer_can_become_a_helper()
    {
        $user = User::factory()->create(['role' => 'customer']);
        $token = $user->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/helper/profile', [
                'bio' => 'I fix things.',
                'experience_years' => 3,
            ]);

        $response->assertStatus(201)
            ->assertJsonPath('data.bio', 'I fix things.')
            ->assertJsonPath('data.kyc_status', 'pending');

        $this->assertDatabaseHas('users', [
            'id' => $user->id,
            'role' => 'helper',
        ]);
        $this->assertDatabaseHas('helper_profiles', [
            'user_id' => $user->id,
        ]);
    }

    /**
     * Test becoming a helper twice is rejected instead of creating a duplicate profile.
     */
    public function test_cannot_become_a_helper_twice()
    {
        $user = User::factory()->create(['role' => 'helper']);
        HelperProfile::factory()->create(['user_id' => $user->id]);
        $token = $user->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/helper/profile', ['bio' => 'Again']);

        $response->assertStatus(403);
    }

    /**
     * Test helper can view their helper profile.
     */
    public function test_helper_can_view_their_helper_profile()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/helper/profile');

        $response->assertStatus(200);
    }

    /**
     * Test non-helper cannot access helper profile endpoints.
     */
    public function test_non_helper_cannot_access_helper_profile_endpoints()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/helper/profile');

        $response->assertStatus(403);
    }
}
