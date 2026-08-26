<?php

namespace App\Http\Controllers;

use App\Http\Requests\UpdateProfileRequest;
use App\Http\Resources\UserResource;
use Illuminate\Http\Request;

class UserController extends Controller
{
    /**
     * Get authenticated user profile.
     */
    public function show(Request $request)
    {
        return new UserResource($request->user());
    }

    /**
     * Update authenticated user profile.
     */
    public function update(UpdateProfileRequest $request)
    {
        $user = $request->user();

        $user->update($request->only([
            'name',
            'email',
            'profile_photo_url',
            'address',
        ]));

        return new UserResource($user);
    }
}
