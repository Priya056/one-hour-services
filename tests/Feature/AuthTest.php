<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AuthTest extends TestCase
{
    use RefreshDatabase;

    /**
     * Test user registration.
     */
    public function test_user_can_register()
    {
        $response = $this->postJson('/api/register', [
            'name' => 'Test User',
            'phone' => '+1234567890',
            'email' => 'test@example.com',
            'password' => 'password123',
            'role' => 'customer',
        ]);

        $response->assertStatus(201)
            ->assertJsonStructure([
                'user' => ['id', 'name', 'phone', 'email', 'role'],
                'token',
            ])
            ->assertJsonPath('user.phone', '+1234567890');

        $this->assertDatabaseHas('users', [
            'phone' => '+1234567890',
            'email' => 'test@example.com',
            'role' => 'customer',
        ]);
    }

    /**
     * Test registration with duplicate phone.
     */
    public function test_registration_fails_with_duplicate_phone()
    {
        User::factory()->create([
            'phone' => '+1234567890',
        ]);

        $response = $this->postJson('/api/register', [
            'name' => 'Test User',
            'phone' => '+1234567890',
            'email' => 'test2@example.com',
            'password' => 'password123',
            'role' => 'customer',
        ]);

        $response->assertStatus(422)
            ->assertJsonValidationErrors(['phone']);
    }

    /**
     * Test registration with duplicate email.
     */
    public function test_registration_fails_with_duplicate_email()
    {
        User::factory()->create([
            'email' => 'test@example.com',
        ]);

        $response = $this->postJson('/api/register', [
            'name' => 'Test User',
            'phone' => '+1234567891',
            'email' => 'test@example.com',
            'password' => 'password123',
            'role' => 'customer',
        ]);

        $response->assertStatus(422)
            ->assertJsonValidationErrors(['email']);
    }

    /**
     * Test user login.
     */
    public function test_user_can_login()
    {
        $user = User::factory()->create([
            'phone' => '+1234567890',
            'password_hash' => bcrypt('password123'),
            'is_active' => true,
        ]);

        $response = $this->postJson('/api/login', [
            'phone' => '+1234567890',
            'password' => 'password123',
        ]);

        $response->assertStatus(200)
            ->assertJsonStructure([
                'user' => ['id', 'name', 'phone', 'role'],
                'token',
            ])
            ->assertJsonPath('user.phone', '+1234567890');
    }

    /**
     * Test login with invalid credentials.
     */
    public function test_login_fails_with_invalid_credentials()
    {
        User::factory()->create([
            'phone' => '+1234567890',
            'password_hash' => bcrypt('password123'),
        ]);

        $response = $this->postJson('/api/login', [
            'phone' => '+1234567890',
            'password' => 'wrongpassword',
        ]);

        $response->assertStatus(422);
    }

    /**
     * Test login with inactive user.
     */
    public function test_login_fails_for_inactive_user()
    {
        $user = User::factory()->create([
            'phone' => '+1234567890',
            'password_hash' => bcrypt('password123'),
            'is_active' => false,
        ]);

        $response = $this->postJson('/api/login', [
            'phone' => '+1234567890',
            'password' => 'password123',
        ]);

        $response->assertStatus(403)
            ->assertJson(['message' => 'Your account is inactive. Please contact support.']);
    }

    /**
     * Test user logout.
     */
    public function test_user_can_logout()
    {
        $user = User::factory()->create();
        $token = $user->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->postJson('/api/logout');

        $response->assertStatus(200)
            ->assertJson(['message' => 'Successfully logged out']);
    }

    /**
     * Test getting authenticated user.
     */
    public function test_authenticated_user_can_get_profile()
    {
        $user = User::factory()->create();
        $token = $user->createToken('auth-token')->plainTextToken;

        $response = $this->withToken($token)
            ->getJson('/api/user');

        $response->assertStatus(200)
            ->assertJsonPath('id', $user->id)
            ->assertJsonPath('name', $user->name)
            ->assertJsonPath('phone', $user->phone);
    }
}
