<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateReviewRequest;
use App\Http\Resources\ReviewResource;
use App\Models\Review;
use App\Models\Booking;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ReviewController extends Controller
{
    use AuthorizesRequests;

    /**
     * Create a review for a booking.
     */
    public function store(CreateReviewRequest $request)
    {
        $booking = Booking::findOrFail($request->booking_id);

        // Validate that the authenticated user is the customer of this booking
        if ($request->user()->id !== $booking->customer_id) {
            return response()->json([
                'message' => 'You can only review your own bookings.',
            ], 403);
        }

        // Validate that the helper receiving the review is the helper from the booking
        if ($request->helper_id !== $booking->helper_id) {
            return response()->json([
                'message' => 'The helper must match the booking.',
            ], 422);
        }

        return DB::transaction(function () use ($request, $booking) {
            try {
                $review = Review::create([
                    'booking_id' => $booking->id,
                    'customer_id' => $booking->customer_id,
                    'helper_id' => $booking->helper_id,
                    'rating' => $request->rating,
                    'comment' => $request->comment,
                ]);

                return new ReviewResource($review->load(['customer', 'helper', 'booking']));
            } catch (\Illuminate\Database\QueryException $e) {
                if ($e->getCode() === '23000') { // Unique constraint violation
                    return response()->json([
                        'message' => 'A review for this booking already exists.',
                    ], 422);
                }
                throw $e;
            }
        });
    }

    /**
     * Get a specific review.
     */
    public function show(Request $request, $id)
    {
        $review = Review::with(['customer', 'helper', 'booking'])->findOrFail($id);

        $this->authorize('view', $review);

        return new ReviewResource($review);
    }

    /**
     * Get reviews for a specific helper (public endpoint).
     */
    public function helperReviews(Request $request, $helperId)
    {
        $reviews = Review::where('helper_id', $helperId)
            ->with(['customer', 'booking'])
            ->orderBy('created_at', 'desc')
            ->get();

        return ReviewResource::collection($reviews);
    }
}
