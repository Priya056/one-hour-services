<?php

namespace App\Policies;

use App\Models\HelperAvailability;
use App\Models\User;
use Illuminate\Auth\Access\HandlesAuthorization;

class HelperAvailabilityPolicy
{
    use HandlesAuthorization;

    /**
     * Determine if the user can view the helper availability.
     */
    public function view(User $user, HelperAvailability $availability): bool
    {
        // Helpers can view their own availability
        // Admins can view any availability
        return $user->id === $availability->helper->user_id || $user->isAdmin();
    }

    /**
     * Determine if the user can create availability.
     */
    public function create(User $user): bool
    {
        // Only helpers can create their own availability
        return $user->isHelper();
    }

    /**
     * Determine if the user can update the helper availability.
     */
    public function update(User $user, HelperAvailability $availability): bool
    {
        // Helpers can only update their own availability
        return $user->id === $availability->helper->user_id;
    }

    /**
     * Determine if the user can delete the helper availability.
     */
    public function delete(User $user, HelperAvailability $availability): bool
    {
        // Helpers can only delete their own availability
        return $user->id === $availability->helper->user_id;
    }
}
