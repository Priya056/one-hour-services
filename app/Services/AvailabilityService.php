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

        // Check if current time falls within the availability window
        $startTime = $availability->start_time;
        $endTime = $availability->end_time;

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
}
