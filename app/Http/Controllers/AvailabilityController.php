<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateAvailabilityRequest;
use App\Http\Requests\UpdateAvailabilityRequest;
use App\Http\Resources\HelperAvailabilityResource;
use App\Http\Resources\NearbyHelperResource;
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
     * Update the authenticated helper's live location.
     * The Helper app is expected to call this periodically while "available now" is on.
     */
    public function updateLocation(Request $request)
    {
        $user = $request->user();
        $helperProfile = $user->helperProfile;

        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        $validated = $request->validate([
            'lat' => ['required', 'numeric', 'between:-90,90'],
            'lng' => ['required', 'numeric', 'between:-180,180'],
        ]);

        $helperProfile->update([
            'current_lat' => $validated['lat'],
            'current_lng' => $validated['lng'],
            'location_updated_at' => now(),
        ]);

        return response()->json([
            'current_lat' => $helperProfile->current_lat,
            'current_lng' => $helperProfile->current_lng,
            'location_updated_at' => $helperProfile->location_updated_at,
        ]);
    }

    /**
     * Search for available helpers near a given point ("who is available near me right now").
     */
    public function nearby(Request $request)
    {
        $validated = $request->validate([
            'lat' => ['required', 'numeric', 'between:-90,90'],
            'lng' => ['required', 'numeric', 'between:-180,180'],
            'category_id' => ['nullable', 'integer', 'exists:categories,id'],
            'max_distance_km' => ['nullable', 'numeric', 'min:0', 'max:100'],
        ]);

        $helpers = $this->availabilityService->getNearbyAvailableHelpers(
            (float) $validated['lat'],
            (float) $validated['lng'],
            (float) ($validated['max_distance_km'] ?? 25.0),
            $validated['category_id'] ?? null,
        );

        return NearbyHelperResource::collection($helpers);
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
