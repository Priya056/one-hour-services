<?php

namespace Database\Factories;

use App\Models\Booking;
use App\Models\User;
use App\Models\HelperProfile;
use App\Models\Category;
use Illuminate\Database\Eloquent\Factories\Factory;

class BookingFactory extends Factory
{
    protected $model = Booking::class;

    public function definition(): array
    {
        $customer = User::factory()->create(['role' => 'customer']);
        $helper = User::factory()->create(['role' => 'helper']);
        $helperProfile = HelperProfile::factory()->create(['user_id' => $helper->id]);
        $category = Category::factory()->create();

        return [
            'customer_id' => $customer->id,
            'helper_id' => $helperProfile->id,
            'category_id' => $category->id,
            'scheduled_time' => $this->faker->dateTimeBetween('+1 day', '+7 days'),
            'duration_hours' => (float) $this->faker->randomFloat(1, 1, 8),
            'status' => 'requested',
            'location_lat' => $this->faker->latitude(-90, 90),
            'location_lng' => $this->faker->longitude(-180, 180),
            'address_text' => $this->faker->address,
            'total_amount' => (float) $this->faker->randomFloat(2, 100, 5000),
        ];
    }
}
