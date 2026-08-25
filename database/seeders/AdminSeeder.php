<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Seeder;

class AdminSeeder extends Seeder
{
    /**
     * Self-registration deliberately blocks the admin role (RegisterRequest),
     * so without a seeded account nobody could ever reach the admin routes
     * that approve helper KYC — which every booking requires. Seed one
     * default admin so that path always exists after a fresh deploy.
     *
     * Credentials come from env so nothing secret is committed. Set
     * ADMIN_SEED_PHONE / ADMIN_SEED_PASSWORD in Render's environment before
     * deploying (falls back to a placeholder locally).
     */
    public function run(): void
    {
        User::firstOrCreate(
            ['phone' => env('ADMIN_SEED_PHONE', '9999999999')],
            [
                'name' => 'Platform Admin',
                'email' => 'admin@onehour.local',
                'password_hash' => env('ADMIN_SEED_PASSWORD', 'local-dev-only'),
                'role' => 'admin',
                'is_active' => true,
            ]
        );
    }
}
