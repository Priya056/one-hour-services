<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class CategorySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $categories = [
            [
                'id' => 1,
                'name' => 'Personal Assistance',
                'description' => 'Personal assistants, administrative help, scheduling, organizing',
            ],
            [
                'id' => 2,
                'name' => 'Electrical',
                'description' => 'Minor electrical repairs, fixture installations, wiring troubleshooting',
            ],
            [
                'id' => 3,
                'name' => 'Tutoring',
                'description' => '1-hour subject tutoring, language practice, assignment help',
            ],
            [
                'id' => 4,
                'name' => 'Photography',
                'description' => 'Event photography, quick portrait sessions, product photos',
            ],
            [
                'id' => 5,
                'name' => 'Home Repairs',
                'description' => 'Handyman services, furniture assembly, plumbing repairs',
            ],
            [
                'id' => 6,
                'name' => 'Errands & Delivery',
                'description' => 'Pick up and drop off, local errands, grocery shopping',
            ],
            [
                'id' => 7,
                'name' => 'Design/Creative',
                'description' => 'Graphic design edits, video clipping, quick logo adjustments',
            ],
            [
                'id' => 8,
                'name' => 'Business/Professional',
                'description' => 'Resume editing, document formatting, tech support',
            ],
        ];

        DB::table('categories')->insert($categories);
    }
}
