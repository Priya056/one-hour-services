<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateHelperServiceRequest;
use App\Http\Requests\UpdateHelperServiceRequest;
use App\Http\Resources\HelperServiceResource;
use App\Models\HelperService;
use Illuminate\Http\Request;

class HelperServiceController extends Controller
{
    /**
     * Get authenticated helper's services.
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

        $services = $helperProfile->helperServices()->with('category')->get();

        return HelperServiceResource::collection($services);
    }

    /**
     * Create a new helper service.
     */
    public function store(CreateHelperServiceRequest $request)
    {
        $user = $request->user();
        $helperProfile = $user->helperProfile;

        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        // Check if service already exists for this category
        $existingService = $helperProfile->helperServices()
            ->where('category_id', $request->category_id)
            ->first();

        if ($existingService) {
            return response()->json([
                'message' => 'Service for this category already exists.',
            ], 422);
        }

        $service = HelperService::create([
            'helper_id' => $helperProfile->id,
            'category_id' => $request->category_id,
            'hourly_rate' => $request->hourly_rate,
            'is_active' => true,
        ]);

        return new HelperServiceResource($service->load('category'));
    }

    /**
     * Update helper service.
     */
    public function update(UpdateHelperServiceRequest $request, $id)
    {
        $user = $request->user();
        $service = HelperService::findOrFail($id);

        // Check ownership
        if ($user->id !== $service->helper->user_id) {
            return response()->json([
                'message' => 'Unauthorized.',
            ], 403);
        }

        $service->update($request->only(['hourly_rate', 'is_active']));

        return new HelperServiceResource($service->load('category'));
    }

    /**
     * Delete helper service.
     */
    public function destroy(Request $request, $id)
    {
        $user = $request->user();
        $service = HelperService::findOrFail($id);

        // Check ownership
        if ($user->id !== $service->helper->user_id) {
            return response()->json([
                'message' => 'Unauthorized.',
            ], 403);
        }

        $service->delete();

        return response()->json([
            'message' => 'Service deleted successfully.',
        ]);
    }
}
