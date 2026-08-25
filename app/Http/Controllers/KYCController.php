<?php

namespace App\Http\Controllers;

use App\Http\Requests\SubmitKYCRequest;
use App\Http\Resources\KYCDocumentResource;
use App\Models\KYCDocument;
use Illuminate\Http\Request;

class KYCController extends Controller
{
    /**
     * Submit KYC document.
     */
    public function submit(SubmitKYCRequest $request)
    {
        $user = $request->user();
        $helperProfile = $user->helperProfile;

        if (!$helperProfile) {
            return response()->json([
                'message' => 'Helper profile not found.',
            ], 404);
        }

        $kycDocument = KYCDocument::create([
            'helper_id' => $helperProfile->id,
            'document_type' => $request->document_type,
            'document_url' => $request->document_url,
            'status' => 'pending',
        ]);

        return new KYCDocumentResource($kycDocument);
    }

    /**
     * Get authenticated helper's KYC documents.
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

        $documents = $helperProfile->kycDocuments;

        return KYCDocumentResource::collection($documents);
    }

    /**
     * Get specific KYC document.
     */
    public function show(Request $request, $id)
    {
        $user = $request->user();
        $document = KYCDocument::findOrFail($id);

        // Check authorization
        if ($user->id !== $document->helper->user_id && !$user->isAdmin()) {
            return response()->json([
                'message' => 'Unauthorized.',
            ], 403);
        }

        return new KYCDocumentResource($document);
    }

    /**
     * Admin: Approve KYC document.
     */
    public function approve(Request $request, $id)
    {
        $document = KYCDocument::findOrFail($id);

        $document->update([
            'status' => 'approved',
            'reviewed_by' => $request->user()->id,
            'reviewed_at' => now(),
        ]);

        // Only mark the profile approved once every document is approved.
        // Checking for "no pending" alone would also flip an already-rejected
        // profile back to approved as soon as its last pending doc clears.
        $helperProfile = $document->helper;
        $hasOutstandingDocuments = $helperProfile->kycDocuments()
            ->whereIn('status', ['pending', 'rejected'])
            ->exists();

        if (!$hasOutstandingDocuments) {
            $helperProfile->update(['kyc_status' => 'approved']);
        }

        return new KYCDocumentResource($document);
    }

    /**
     * Admin: Reject KYC document.
     */
    public function reject(Request $request, $id)
    {
        $document = KYCDocument::findOrFail($id);

        $document->update([
            'status' => 'rejected',
            'reviewed_by' => $request->user()->id,
            'reviewed_at' => now(),
        ]);

        // Update helper profile KYC status to rejected
        $document->helper->update(['kyc_status' => 'rejected']);

        return new KYCDocumentResource($document);
    }
}
