<?php

namespace Database\Factories;

use App\Models\KYCDocument;
use App\Models\HelperProfile;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<KYCDocument>
 */
class KYCDocumentFactory extends Factory
{
    /**
     * Define the model's default state.
     */
    public function definition(): array
    {
        return [
            'helper_id' => HelperProfile::factory(),
            'document_type' => fake()->randomElement(['Aadhaar', 'PAN', 'Driving License', 'Passport']),
            'document_url' => fake()->url(),
            'status' => fake()->randomElement(['pending', 'approved', 'rejected']),
            'reviewed_by' => null,
            'reviewed_at' => null,
        ];
    }
}
