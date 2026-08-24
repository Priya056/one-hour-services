<?php

namespace App\Policies;

use App\Models\HelperService;
use App\Models\User;
use Illuminate\Auth\Access\HandlesAuthorization;

class HelperServicePolicy
{
    use HandlesAuthorization;

    /**
     * Determine if the user can view the helper service.
     */
    public function view(User $user, HelperService $service): bool
    {
        // Helpers can view their own services
        // Admins can view any service
        return $user->id === $service->helper->user_id || $user->isAdmin();
    }

    /**
     * Determine if the user can create helper services.
     */
    public function create(User $user): bool
    {
        // Only helpers can create their own services
        return $user->isHelper();
    }

    /**
     * Determine if the user can update the helper service.
     */
    public function update(User $user, HelperService $service): bool
    {
        // Helpers can only update their own services
        return $user->id === $service->helper->user_id;
    }

    /**
     * Determine if the user can delete the helper service.
     */
    public function delete(User $user, HelperService $service): bool
    {
        // Helpers can only delete their own services
        return $user->id === $service->helper->user_id;
    }
}
