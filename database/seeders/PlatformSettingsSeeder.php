<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class PlatformSettingsSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $settings = [
            [
                'key' => 'default_commission_percent',
                'value' => '15.00',
                'description' => 'Percentage taken by the platform on each completed booking',
            ],
            [
                'key' => 'max_search_radius_km',
                'value' => '25.00',
                'description' => 'Maximum radius allowed for nearby helper lookup',
            ],
            [
                'key' => 'booking_cancellation_window_mins',
                'value' => '15',
                'description' => 'Free cancellation window in minutes after booking acceptance',
            ],
        ];

        DB::table('platform_settings')->insert($settings);
    }
}
