<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateAvailabilityRequest;
use App\Http\Requests\UpdateAvailabilityRequest;
use App\Http\Resources\HelperAvailabilityResource;
use App\Models\HelperAvailability;
use App\Services\AvailabilityService;
use Illuminate\Http\Request;

class AvailabilityController extends Controller
{
    protected AvailabilityService $availabilityService;

    public function __construct(AvailabilityService $availabilityService)
    {
        $this->availabilityService = $availabilityService;
    }

    /**
     * Get authenticated helper's availability schedule.
     */
    public function index(Request $request)
    {
        $user = $request->user();
        $helperProfile = $user->helperProfile;

        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        $availability = $helperProfile->helperAvailability()->orderBy('day_of_week')->get();

        return HelperAvailabilityResource::collection($availability);
    }

    /**
     * Create a new availability slot.
     */
    public function store(CreateAvailabilityRequest $request)
    {
        $user = $request->user();
        $helperProfile = $user->helperProfile;

        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        // Check if availability already exists for this day
        $existing = HelperAvailability::where('helper_id', $helperProfile->id)
            ->where('day_of_week', $request->day_of_week)
            ->first();

        if ($existing) {
            return response()->json([
                'message' => 'Availability for this day already exists. Use update instead.',
            ], 422);
        }

        $availability = HelperAvailability::create([
            'helper_id' => $helperProfile->id,
            'day_of_week' => $request->day_of_week,
            'start_time' => $request->start_time,
            'end_time' => $request->end_time,
        ]);

        return new HelperAvailabilityResource($availability);
    }

    /**
     * Update availability slot.
     */
    public function update(UpdateAvailabilityRequest $request, $id)
    {
        $user = $request->user();
        $availability = HelperAvailability::findOrFail($id);

        // Check ownership
        if ($user->id !== $availability->helper->user_id) {
            return response()->json([
                'message' => 'Unauthorized.',
            ], 403);
        }

        $availability->update($request->only(['day_of_week', 'start_time', 'end_time']));

        return new HelperAvailabilityResource($availability);
    }

    /**
     * Delete availability slot.
     */
    public function destroy(Request $request, $id)
    {
        $user = $request->user();
        $availability = HelperAvailability::findOrFail($id);

        // Check ownership
        if ($user->id !== $availability->helper->user_id) {
            return response()->json([
                'message' => 'Unauthorized.',
            ], 403);
        }

        $availability->delete();

        return response()->json([
            'message' => 'Availability slot deleted successfully.',
        ]);
    }

    /**
     * Toggle available-now status.
     */
    public function toggleAvailableNow(Request $request)
    {
        $user = $request->user();
        $helperProfile = $user->helperProfile;

        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        $request->validate([
            'is_available_now' => ['required', 'boolean'],
        ]);

        $helperProfile->update([
            'is_available_now' => $request->is_available_now,
        ]);

        return response()->json([
            'is_available_now' => $helperProfile->is_available_now,
        ]);
    }
}
