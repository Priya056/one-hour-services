<?php

namespace App\Http\Controllers;

use App\Http\Resources\ComplaintDisputeResource;
use App\Models\ComplaintDispute;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Http\Request;

class DisputeController extends Controller
{
    use AuthorizesRequests;

    /**
     * Get all disputes (admin only)
     */
    public function index(Request $request)
    {
        if (!$request->user()->isAdmin()) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $disputes = ComplaintDispute::with(['booking', 'booking.customer', 'booking.helper', 'raisedBy', 'resolvedBy'])
            ->orderBy('created_at', 'desc')
            ->get();

        return ComplaintDisputeResource::collection($disputes);
    }

    /**
     * Get dispute details (admin only)
     */
    public function show(Request $request, $id)
    {
        if (!$request->user()->isAdmin()) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $dispute = ComplaintDispute::with(['booking', 'booking.customer', 'booking.helper', 'raisedBy', 'resolvedBy'])
            ->findOrFail($id);

        return new ComplaintDisputeResource($dispute);
    }

    /**
     * Resolve dispute (admin only)
     */
    public function resolve(Request $request, $id)
    {
        if (!$request->user()->isAdmin()) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $request->validate([
            'resolution_notes' => ['nullable', 'string'],
        ]);

        $dispute = ComplaintDispute::findOrFail($id);

        if ($dispute->status === 'resolved') {
            return response()->json([
                'message' => 'Dispute is already resolved.',
            ], 422);
        }

        $dispute->update([
            'status' => 'resolved',
            'resolved_by' => $request->user()->id,
            'resolved_at' => now(),
        ]);

        return new ComplaintDisputeResource($dispute->load(['resolvedBy']));
    }
}
