<?php

namespace Database\Factories;

use App\Models\HelperAvailability;
use App\Models\HelperProfile;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<HelperAvailability>
 */
class HelperAvailabilityFactory extends Factory
{
    /**
     * Define the model's default state.
     */
    public function definition(): array
    {
        return [
            'helper_id' => HelperProfile::factory(),
            'day_of_week' => fake()->numberBetween(0, 6),
            'start_time' => fake()->time('H:i:s'),
            'end_time' => fake()->time('H:i:s'),
        ];
    }
}
