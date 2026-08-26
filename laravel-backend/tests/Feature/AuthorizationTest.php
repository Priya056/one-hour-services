<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AuthorizationTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Test customer can access customer endpoints.
     */
    public function test_customer_can_access_own_profile()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/profile');

        $response->assertStatus(200);
    }

    /**
     * Test helper cannot access admin endpoints.
     */
    public function test_helper_cannot_access_admin_endpoints()
    {
        $helper = User::factory()->create(['role' => 'helper']);
        $token = $helper->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/admin/helpers');

        $response->assertStatus(403);
    }

    /**
     * Test admin can access admin endpoints.
     */
    public function test_admin_can_access_admin_endpoints()
    {
        $admin = User::factory()->create(['role' => 'admin']);
        $token = $admin->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/admin/helpers');

        $response->assertStatus(200);
    }

    /**
     * Test unauthorized role attempt on admin endpoint.
     */
    public function test_unauthorized_role_attempt_on_admin_endpoint()
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $token = $customer->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->patchJson('/api/admin/helpers/1/approve');

        $response->assertStatus(403);
    }
}
