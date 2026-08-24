<?php

namespace App\Policies;

use App\Models\User;
use Illuminate\Auth\Access\HandlesAuthorization;

class UserPolicy
{
    use HandlesAuthorization;

    /**
     * Determine if the user can view the model.
     */
    public function view(User $user, User $model): bool
    {
        // Users can view their own profile
        // Admins can view any profile
        return $user->id === $model->id || $user->isAdmin();
    }

    /**
     * Determine if the user can update the model.
     */
    public function update(User $user, User $model): bool
    {
        // Users can only update their own profile
        // Admins can update any profile
        return $user->id === $model->id || $user->isAdmin();
    }

    /**
     * Determine if the user can delete the model.
     */
    public function delete(User $user, User $model): bool
    {
        // Only admins can delete users
        return $user->isAdmin();
    }
}
