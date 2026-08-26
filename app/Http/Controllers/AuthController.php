<?php

namespace App\Http\Controllers;

use App\Http\Requests\FirebaseLoginRequest;
use App\Http\Requests\LoginRequest;
use App\Http\Requests\RegisterRequest;
use App\Http\Resources\UserResource;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;
use Kreait\Firebase\Exception\AuthException as FirebaseAuthException;
use Kreait\Laravel\Firebase\Facades\Firebase;

class AuthController extends Controller
{
    /**
     * Register a new user.
     */
    public function register(RegisterRequest $request)
    {
        $user = User::create([
            'name' => $request->name,
            'phone' => $request->phone,
            'email' => $request->email,
            'password_hash' => Hash::make($request->password),
            'role' => $request->role,
            'is_active' => true,
        ]);

        $token = $user->createToken('auth-token')->plainTextToken;

        return response()->json([
            'user' => new UserResource($user),
            'token' => $token,
        ], 201);
    }

    /**
     * Login user and create token.
     */
    public function login(LoginRequest $request)
    {
        $user = User::where('phone', $request->phone)->first();

        if (!$user || !Hash::check($request->password, $user->password_hash)) {
            throw ValidationException::withMessages([
                'phone' => ['The provided credentials are incorrect.'],
            ]);
        }

        if (!$user->is_active) {
            return response()->json([
                'message' => 'Your account is inactive. Please contact support.',
            ], 403);
        }

        $token = $user->createToken('auth-token')->plainTextToken;

        return response()->json([
            'user' => new UserResource($user),
            'token' => $token,
        ]);
    }

    /**
     * Verify a Firebase Phone Auth ID token (real SMS OTP, verified
     * client-side by the Firebase SDK) and issue our own Sanctum token.
     * First-time phone numbers are auto-registered as customers — this is
     * the only entry point the customer app's OTP screen uses now.
     */
    public function firebaseLogin(FirebaseLoginRequest $request)
    {
        try {
            $verifiedToken = Firebase::auth()->verifyIdToken($request->id_token);
        } catch (FirebaseAuthException $e) {
            return response()->json([
                'message' => 'Invalid or expired verification code. Please try again.',
            ], 401);
        }

        $rawPhone = $verifiedToken->claims()->get('phone_number');

        if (!$rawPhone) {
            return response()->json([
                'message' => 'This verification token has no phone number attached.',
            ], 422);
        }

        // The app only ever collects +91 numbers (see LoginRegisterScreen),
        // and stores phone as the bare 10-digit number everywhere else.
        $phone = preg_replace('/^\+91/', '', $rawPhone);

        $user = User::firstOrCreate(
            ['phone' => $phone],
            [
                'name' => "Customer {$phone}",
                'email' => null,
                // Sign-in is entirely token-based from here on; this hash is
                // never used to authenticate, just satisfies the column.
                'password_hash' => Hash::make(Str::random(40)),
                'role' => 'customer',
                'is_active' => true,
            ]
        );

        if (!$user->is_active) {
            return response()->json([
                'message' => 'Your account is inactive. Please contact support.',
            ], 403);
        }

        $token = $user->createToken('auth-token')->plainTextToken;

        return response()->json([
            'user' => new UserResource($user),
            'token' => $token,
        ]);
    }

    /**
     * Logout user (revoke token).
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Successfully logged out',
        ]);
    }

    /**
     * Get authenticated user.
     */
    public function user(Request $request)
    {
        return new UserResource($request->user());
    }
}
