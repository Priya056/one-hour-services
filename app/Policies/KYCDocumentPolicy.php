<?php

namespace App\Policies;

use App\Models\KYCDocument;
use App\Models\User;
use Illuminate\Auth\Access\HandlesAuthorization;

class KYCDocumentPolicy
{
    use HandlesAuthorization;

    /**
     * Determine if the user can view the KYC document.
     */
    public function view(User $user, KYCDocument $document): bool
    {
        // Helpers can view their own KYC documents
        // Admins can view any KYC document
        return $user->id === $document->helper->user_id || $user->isAdmin();
    }

    /**
     * Determine if the user can create KYC documents.
     */
    public function create(User $user): bool
    {
        // Only helpers can submit KYC documents
        return $user->isHelper();
    }

    /**
     * Determine if the user can approve the KYC document.
     */
    public function approve(User $user, KYCDocument $document): bool
    {
        // Only admins can approve KYC documents
        return $user->isAdmin();
    }

    /**
     * Determine if the user can reject the KYC document.
     */
    public function reject(User $user, KYCDocument $document): bool
    {
        // Only admins can reject KYC documents
        return $user->isAdmin();
    }
}
