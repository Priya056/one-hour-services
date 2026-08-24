<?php

namespace App\Http\Controllers;

use App\Http\Requests\UpdateHelperProfileRequest;
use App\Http\Resources\HelperProfileResource;
use App\Models\HelperProfile;
use Illuminate\Http\Request;

class HelperController extends Controller
{
    /**
     * Get authenticated helper's profile.
     */
    public function show(Request $request)
    {
        $user = $request->user();
        
        if (!$user->isHelper()) {
            return response()->json([
                'message' => 'Only helpers can access helper profiles.',
            ], 403);
        }

        $profile = $user->helperProfile;
        
        if (!$profile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        return new HelperProfileResource($profile);
    }

    /**
     * Update authenticated helper's profile.
     */
    public function update(UpdateHelperProfileRequest $request)
    {
        $user = $request->user();
        
        if (!$user->isHelper()) {
            return response()->json([
                'message' => 'Only helpers can update helper profiles.',
            ], 403);
        }

        $profile = $user->helperProfile;
        
        if (!$profile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        $profile->update($request->only([
            'bio',
            'experience_years',
            'service_radius_km',
        ]));

        return new HelperProfileResource($profile);
    }

    /**
     * Update helper status (active/inactive).
     */
    public function updateStatus(Request $request)
    {
        $user = $request->user();
        
        if (!$user->isHelper()) {
            return response()->json([
                'message' => 'Only helpers can update their status.',
            ], 403);
        }

        $request->validate([
            'is_active' => ['required', 'boolean'],
        ]);

        $user->update(['is_active' => $request->is_active]);

        return new \App\Http\Resources\UserResource($user);
    }

    /**
     * Admin: List all helpers.
     */
    public function index(Request $request)
    {
        $helpers = HelperProfile::with('user')->get();
        
        return HelperProfileResource::collection($helpers);
    }

    /**
     * Admin: Approve helper.
     */
    public function approve(Request $request, $id)
    {
        $profile = HelperProfile::findOrFail($id);
        
        $profile->update(['kyc_status' => 'approved']);
        
        return new HelperProfileResource($profile);
    }

    /**
     * Admin: Reject helper.
     */
    public function reject(Request $request, $id)
    {
        $profile = HelperProfile::findOrFail($id);
        
        $profile->update(['kyc_status' => 'rejected']);
        
        return new HelperProfileResource($profile);
    }
}
