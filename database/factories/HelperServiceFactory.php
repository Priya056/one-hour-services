<?php

namespace Database\Factories;

use App\Models\HelperService;
use App\Models\HelperProfile;
use App\Models\Category;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<HelperService>
 */
class HelperServiceFactory extends Factory
{
    /**
     * Define the model's default state.
     */
    public function definition(): array
    {
        return [
            'helper_id' => HelperProfile::factory(),
            'category_id' => Category::factory(),
            'hourly_rate' => fake()->randomFloat(2, 100, 2000),
            'is_active' => true,
        ];
    }
}
