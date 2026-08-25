<?php

namespace Database\Factories;

use App\Models\Review;
use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Booking;
use App\Models\Category;
use Illuminate\Database\Eloquent\Factories\Factory;

class ReviewFactory extends Factory
{
    protected $model = Review::class;

    public function definition(): array
    {
        return [
            'booking_id' => Booking::factory(),
            'customer_id' => User::factory()->create(['role' => 'customer']),
            'helper_id' => HelperProfile::factory(),
            'rating' => $this->faker->numberBetween(1, 5),
            'comment' => $this->faker->sentence(),
        ];
    }
}
