<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateBookingRequest;
use App\Http\Requests\UpdateBookingStatusRequest;
use App\Http\Resources\BookingResource;
use App\Models\Booking;
use App\Models\HelperService;
use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class BookingController extends Controller
{
    use AuthorizesRequests;

    /**
     * Create a new booking.
     */
    public function store(CreateBookingRequest $request)
    {
        return DB::transaction(function () use ($request) {
            $user = $request->user();
            $helper = \App\Models\HelperProfile::findOrFail($request->helper_id);
            
            // Get helper's hourly rate for the category
            $service = HelperService::where('helper_id', $helper->id)
                ->where('category_id', $request->category_id)
                ->where('is_active', true)
                ->firstOrFail();

            // Calculate total amount (hourly_rate × duration_hours)
            $totalAmount = bcmul($service->hourly_rate, $request->duration_hours, 2);

            $booking = Booking::create([
                'customer_id' => $user->id,
                'helper_id' => $helper->id,
                'category_id' => $request->category_id,
                'scheduled_time' => $request->scheduled_time,
                'duration_hours' => $request->duration_hours,
                'status' => 'requested',
                'location_lat' => $request->location_lat,
                'location_lng' => $request->location_lng,
                'address_text' => $request->address_text,
                'total_amount' => $totalAmount,
            ]);

            return new BookingResource($booking->load(['customer', 'helper', 'category']));
        });
    }

    /**
     * Get booking details.
     */
    public function show(Request $request, $id)
    {
        $booking = Booking::with(['customer', 'helper', 'category', 'payment', 'review'])
            ->findOrFail($id);

        $this->authorize('view', $booking);

        return new BookingResource($booking);
    }

    /**
     * Get user's booking history.
     */
    public function index(Request $request)
    {
        $user = $request->user();

        if ($user->isCustomer()) {
            $bookings = Booking::forCustomer($user->id)
                ->with(['helper', 'category'])
                ->orderBy('scheduled_time', 'desc')
                ->get();
        } elseif ($user->isHelper()) {
            $helperProfile = $user->helperProfile;
            if (!$helperProfile) {
                return response()->json([
                    'message' => 'Helper profile not found.',
                ], 404);
            }
            $bookings = Booking::forHelper($helperProfile->id)
                ->with(['customer', 'category'])
                ->orderBy('scheduled_time', 'desc')
                ->get();
        } elseif ($user->isAdmin()) {
            $bookings = Booking::with(['customer', 'helper', 'category'])
                ->orderBy('scheduled_time', 'desc')
                ->get();
        } else {
            return response()->json([
                'message' => 'Unauthorized.',
            ], 403);
        }

        return BookingResource::collection($bookings);
    }

    /**
     * Update booking status.
     */
    public function updateStatus(UpdateBookingStatusRequest $request, $id)
    {
        $booking = Booking::with(['helper', 'customer'])->findOrFail($id);

        $this->authorize('update', $booking);

        // Additional check: customer can only cancel
        if ($request->user()->id === $booking->customer_id && $request->status !== 'cancelled') {
            return response()->json([
                'message' => 'Customers can only cancel bookings.',
            ], 403);
        }

        $booking->update(['status' => $request->status]);

        return new BookingResource($booking->load(['customer', 'helper', 'category']));
    }

    /**
     * Cancel booking (via status update).
     */
    public function cancel(Request $request, $id)
    {
        $booking = Booking::findOrFail($id);

        $this->authorize('update', $booking);

        if (!$booking->canTransitionTo('cancelled')) {
            return response()->json([
                'message' => 'Cannot cancel booking in current status.',
            ], 422);
        }

        $booking->update(['status' => 'cancelled']);

        return new BookingResource($booking->load(['customer', 'helper', 'category']));
    }

    /**
     * Delete booking (admin only).
     */
    public function destroy(Request $request, $id)
    {
        $booking = Booking::findOrFail($id);

        $this->authorize('delete', $booking);

        $booking->delete();

        return response()->json([
            'message' => 'Booking deleted successfully.',
        ]);
    }
}
