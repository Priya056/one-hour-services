<?php

namespace Database\Factories;

use App\Models\HelperProfile;
use App\Models\User;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<HelperProfile>
 */
class HelperProfileFactory extends Factory
{
    /**
     * Define the model's default state.
     */
    public function definition(): array
    {
        return [
            'user_id' => User::factory()->helper(),
            'bio' => fake()->optional()->paragraph(),
            'experience_years' => fake()->numberBetween(0, 20),
            'is_available_now' => fake()->boolean(),
            'service_radius_km' => fake()->randomFloat(2, 5, 50),
            'average_rating' => fake()->randomFloat(2, 0, 5),
            'total_reviews' => fake()->numberBetween(0, 100),
            'kyc_status' => fake()->randomElement(['pending', 'approved', 'rejected']),
        ];
    }
}
