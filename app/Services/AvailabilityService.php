<?php

namespace App\Services;

use App\Models\HelperAvailability;
use App\Models\HelperProfile;
use Carbon\Carbon;

class AvailabilityService
{
    /**
     * Check if a helper is available at a specific time.
     */
    public function isAvailableAt(HelperProfile $helper, Carbon $dateTime): bool
    {
        // Check if helper is marked as available now
        if (!$helper->is_available_now) {
            return false;
        }

        // Check if helper has KYC approved
        if ($helper->kyc_status !== 'approved') {
            return false;
        }

        // Check if helper is active
        if (!$helper->user->is_active) {
            return false;
        }

        // Check schedule for the specific day and time
        $dayOfWeek = $dateTime->dayOfWeek; // 0 (Sunday) to 6 (Saturday)
        $currentTime = $dateTime->format('H:i:s');

        $availability = HelperAvailability::where('helper_id', $helper->id)
            ->where('day_of_week', $dayOfWeek)
            ->first();

        if (!$availability) {
            return false;
        }

        // Check if current time falls within the availability window.
        // start_time/end_time are cast to Carbon instances; comparing them
        // directly against $currentTime would coerce via Carbon's default
        // "Y-m-d H:i:s" __toString (not the "H:i:s" cast format used for
        // JSON output), which always sorts greater than a bare time string.
        $startTime = $availability->start_time->format('H:i:s');
        $endTime = $availability->end_time->format('H:i:s');

        return $currentTime >= $startTime && $currentTime <= $endTime;
    }

    /**
     * Check if a helper is available right now.
     */
    public function isAvailableNow(HelperProfile $helper): bool
    {
        return $this->isAvailableAt($helper, Carbon::now());
    }

    /**
     * Get available helpers for a specific time and category.
     */
    public function getAvailableHelpers(Carbon $dateTime, ?int $categoryId = null)
    {
        $dayOfWeek = $dateTime->dayOfWeek;
        $currentTime = $dateTime->format('H:i:s');

        $query = HelperProfile::where('is_available_now', true)
            ->where('kyc_status', 'approved')
            ->whereHas('user', function ($q) {
                $q->where('is_active', true);
            })
            ->whereHas('helperAvailability', function ($q) use ($dayOfWeek, $currentTime) {
                $q->where('day_of_week', $dayOfWeek)
                  ->where('start_time', '<=', $currentTime)
                  ->where('end_time', '>=', $currentTime);
            });

        if ($categoryId) {
            $query->whereHas('helperServices', function ($q) use ($categoryId) {
                $q->where('category_id', $categoryId)
                  ->where('is_active', true);
            });
        }

        return $query->with('user', 'helperServices.category')->get();
    }

    /**
     * Find available helpers near a given point, ordered by distance.
     *
     * SQL only does a cheap lat/lng bounding-box pre-filter (index-friendly,
     * portable across MySQL/SQLite); the exact Haversine distance is computed
     * in PHP and used to drop anything outside the real radius and to sort.
     */
    public function getNearbyAvailableHelpers(
        float $lat,
        float $lng,
        float $maxDistanceKm = 25.0,
        ?int $categoryId = null
    ) {
        $dateTime = Carbon::now();
        $dayOfWeek = $dateTime->dayOfWeek;
        $currentTime = $dateTime->format('H:i:s');

        $latDelta = $maxDistanceKm / 111.0; // ~111km per degree of latitude
        $lngDelta = $maxDistanceKm / (111.0 * max(cos(deg2rad($lat)), 0.01));

        $query = HelperProfile::where('is_available_now', true)
            ->where('kyc_status', 'approved')
            ->whereBetween('current_lat', [$lat - $latDelta, $lat + $latDelta])
            ->whereBetween('current_lng', [$lng - $lngDelta, $lng + $lngDelta])
            ->whereHas('user', fn ($q) => $q->where('is_active', true))
            ->whereHas('helperAvailability', function ($q) use ($dayOfWeek, $currentTime) {
                $q->where('day_of_week', $dayOfWeek)
                  ->where('start_time', '<=', $currentTime)
                  ->where('end_time', '>=', $currentTime);
            });

        if ($categoryId) {
            $query->whereHas('helperServices', function ($q) use ($categoryId) {
                $q->where('category_id', $categoryId)
                  ->where('is_active', true);
            });
        }

        return $query
            ->with(['user', 'helperServices' => function ($q) {
                $q->where('is_active', true)->with('category');
            }])
            ->get()
            ->map(function (HelperProfile $helper) use ($lat, $lng) {
                $helper->distance_km = $this->haversineKm(
                    $lat,
                    $lng,
                    (float) $helper->current_lat,
                    (float) $helper->current_lng
                );

                return $helper;
            })
            ->filter(fn (HelperProfile $helper) => $helper->distance_km <= $maxDistanceKm)
            ->sortBy('distance_km')
            ->values();
    }

    private function haversineKm(float $lat1, float $lng1, float $lat2, float $lng2): float
    {
        $earthRadiusKm = 6371;

        $latDelta = deg2rad($lat2 - $lat1);
        $lngDelta = deg2rad($lng2 - $lng1);

        $a = sin($latDelta / 2) ** 2
            + cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * sin($lngDelta / 2) ** 2;

        return $earthRadiusKm * 2 * atan2(sqrt($a), sqrt(1 - $a));
    }
}
