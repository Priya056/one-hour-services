<?php

namespace App\Policies;

use App\Models\Booking;
use App\Models\User;
use Illuminate\Auth\Access\HandlesAuthorization;

class BookingPolicy
{
    use HandlesAuthorization;

    /**
     * Determine if the user can view the booking.
     */
    public function view(User $user, Booking $booking): bool
    {
        // Customer can view own bookings
        // Helper can view assigned bookings
        // Admin can view all bookings
        return $user->id === $booking->customer_id 
            || $user->id === $booking->helper->user_id 
            || $user->isAdmin();
    }

    /**
     * Determine if the user can create bookings.
     */
    public function create(User $user): bool
    {
        // Only customers can create bookings
        return $user->isCustomer();
    }

    /**
     * Determine if the user can update the booking.
     */
    public function update(User $user, Booking $booking): bool
    {
        // Customer can cancel own bookings
        // Helper can update status of assigned bookings
        // Admin can update any booking
        if ($user->isAdmin()) {
            return true;
        }

        if ($user->id === $booking->customer_id) {
            // Customer can only cancel
            return true; // Allow customer to update (controller validates it's only cancel)
        }

        if ($user->id === $booking->helper->user_id) {
            // Helper can update status
            return true;
        }

        return false;
    }

    /**
     * Determine if the user can delete the booking.
     */
    public function delete(User $user, Booking $booking): bool
    {
        // Only admins can delete bookings (for data management)
        // Cancellation should use status update instead
        return $user->isAdmin();
    }
}
