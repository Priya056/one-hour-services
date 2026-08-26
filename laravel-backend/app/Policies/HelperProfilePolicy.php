<?php

namespace App\Policies;

use App\Models\HelperProfile;
use App\Models\User;
use Illuminate\Auth\Access\HandlesAuthorization;

class HelperProfilePolicy
{
    use HandlesAuthorization;

    /**
     * Determine if the user can view the helper profile.
     */
    public function view(User $user, HelperProfile $profile): bool
    {
        // Helpers can view their own profile
        // Admins can view any helper profile
        return $user->id === $profile->user_id || $user->isAdmin();
    }

    /**
     * Determine if the user can update the helper profile.
     */
    public function update(User $user, HelperProfile $profile): bool
    {
        // Helpers can only update their own profile
        return $user->id === $profile->user_id;
    }

    /**
     * Determine if the user can approve the helper.
     */
    public function approve(User $user, HelperProfile $profile): bool
    {
        // Only admins can approve helpers
        return $user->isAdmin();
    }

    /**
     * Determine if the user can reject the helper.
     */
    public function reject(User $user, HelperProfile $profile): bool
    {
        // Only admins can reject helpers
        return $user->isAdmin();
    }
}
