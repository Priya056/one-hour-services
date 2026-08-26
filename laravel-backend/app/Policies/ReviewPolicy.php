<?php

namespace App\Policies;

use App\Models\Review;
use App\Models\User;
use Illuminate\Auth\Access\HandlesAuthorization;

class ReviewPolicy
{
    use HandlesAuthorization;

    /**
     * Determine if the user can view the review.
     */
    public function view(User $user, Review $review): bool
    {
        // Customer can view their own reviews
        // Helper can view reviews about them
        // Admin can view all reviews
        return $user->id === $review->customer_id 
            || $user->id === $review->helper->user_id 
            || $user->isAdmin();
    }
}
