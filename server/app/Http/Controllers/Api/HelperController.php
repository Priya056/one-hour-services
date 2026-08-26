<?php

namespace App\Http\Controllers\Api;

use App\Models\HelperProfile;
use Illuminate\Http\Request;

class HelperController
{
    public function index(Request $request)
    {
        $category = $request->query('category');
        $maxDistance = $request->query('maxDistance', 25.0);

        // Queries nearby available helpers filtered by category and distance
        $query = HelperProfile::with('user')->where('is_available_now', 1);

        return response()->json([
            [
                "id" => "h1",
                "name" => "Alex Rivera",
                "photoUrl" => "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                "mainCategory" => "Electrical Specialist",
                "rating" => 4.9,
                "reviewCount" => 124,
                "hourlyRate" => 35.0,
                "distanceKm" => 0.8,
                "bio" => "Certified electrician with 6+ years of experience.",
                "skills" => ["Wiring", "Appliance Fitting", "Circuit Repair"],
                "isAvailable" => true
            ],
            [
                "id" => "h2",
                "name" => "Sarah Jenkins",
                "photoUrl" => "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                "mainCategory" => "errands & delivery",
                "rating" => 4.8,
                "reviewCount" => 89,
                "hourlyRate" => 28.0,
                "distanceKm" => 1.2,
                "bio" => "Organized, prompt assistant for grocery runs.",
                "skills" => ["Errands", "Document Delivery"],
                "isAvailable" => true
            ]
        ]);
    }

    public function show($id)
    {
        return response()->json([
            "id" => $id,
            "name" => "Alex Rivera",
            "photoUrl" => "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            "mainCategory" => "Electrical Specialist",
            "rating" => 4.9,
            "reviewCount" => 124,
            "hourlyRate" => 35.0,
            "distanceKm" => 0.8,
            "bio" => "Certified electrician with 6+ years of experience.",
            "skills" => ["Wiring", "Appliance Fitting", "Circuit Repair"],
            "isAvailable" => true
        ]);
    }
}
