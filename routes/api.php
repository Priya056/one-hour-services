<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

// Authentication Routes
Route::post('/register', App\Http\Controllers\AuthController::class . '@register');
Route::post('/login', App\Http\Controllers\AuthController::class . '@login');
Route::post('/logout', App\Http\Controllers\AuthController::class . '@logout')->middleware('auth:sanctum');

// Protected Routes (require authentication)
Route::middleware('auth:sanctum')->group(function () {
    // User Profile Routes
    Route::get('/profile', App\Http\Controllers\UserController::class . '@show');
    Route::put('/profile', App\Http\Controllers\UserController::class . '@update');

    // Helper Profile Routes
    Route::post('/helper/profile', App\Http\Controllers\HelperController::class . '@store');
    Route::get('/helper/profile', App\Http\Controllers\HelperController::class . '@show');
    Route::put('/helper/profile', App\Http\Controllers\HelperController::class . '@update');
    Route::patch('/helper/status', App\Http\Controllers\HelperController::class . '@updateStatus');

    // Helper Availability Routes
    Route::get('/helper/availability', App\Http\Controllers\AvailabilityController::class . '@index');
    Route::post('/helper/availability', App\Http\Controllers\AvailabilityController::class . '@store');
    Route::put('/helper/availability/{id}', App\Http\Controllers\AvailabilityController::class . '@update');
    Route::delete('/helper/availability/{id}', App\Http\Controllers\AvailabilityController::class . '@destroy');
    Route::patch('/helper/available-now', App\Http\Controllers\AvailabilityController::class . '@toggleAvailableNow');
    Route::patch('/helper/location', App\Http\Controllers\AvailabilityController::class . '@updateLocation');

    // KYC Routes
    Route::post('/kyc', App\Http\Controllers\KYCController::class . '@submit');
    Route::get('/kyc', App\Http\Controllers\KYCController::class . '@index');
    Route::get('/kyc/{id}', App\Http\Controllers\KYCController::class . '@show');

    // Helper Service Routes
    Route::get('/helper/services', App\Http\Controllers\HelperServiceController::class . '@index');
    Route::post('/helper/services', App\Http\Controllers\HelperServiceController::class . '@store');
    Route::put('/helper/services/{id}', App\Http\Controllers\HelperServiceController::class . '@update');
    Route::delete('/helper/services/{id}', App\Http\Controllers\HelperServiceController::class . '@destroy');

    // Booking Routes
    Route::get('/bookings', App\Http\Controllers\BookingController::class . '@index');
    Route::post('/bookings', App\Http\Controllers\BookingController::class . '@store');
    Route::get('/bookings/{id}', App\Http\Controllers\BookingController::class . '@show');
    Route::patch('/bookings/{id}/status', App\Http\Controllers\BookingController::class . '@updateStatus');
    Route::patch('/bookings/{id}/cancel', App\Http\Controllers\BookingController::class . '@cancel');
    Route::delete('/bookings/{id}', App\Http\Controllers\BookingController::class . '@destroy');

    // Review Routes
    Route::post('/reviews', App\Http\Controllers\ReviewController::class . '@store');
    Route::get('/reviews/{id}', App\Http\Controllers\ReviewController::class . '@show');

    // Payment Routes
    Route::post('/payments/order', App\Http\Controllers\PaymentController::class . '@createOrder');
    Route::post('/payments', App\Http\Controllers\PaymentController::class . '@store');
    Route::get('/payments/{id}', App\Http\Controllers\PaymentController::class . '@show');

    // Wallet Routes
    Route::get('/wallet', App\Http\Controllers\WalletController::class . '@show');
    Route::post('/wallet/withdraw', App\Http\Controllers\WalletController::class . '@withdraw');
});

// Public Routes (no authentication required)
Route::get('/helpers/{helperId}/reviews', App\Http\Controllers\ReviewController::class . '@helperReviews');

// Webhook Routes (no authentication, signature verified in controller)
Route::post('/webhooks/razorpay', App\Http\Controllers\WebhookController::class . '@razorpay');

// Admin Routes (require admin role)
Route::middleware(['auth:sanctum', 'role:admin'])->group(function () {
    // Admin Helper Management
    Route::get('/admin/helpers', App\Http\Controllers\HelperController::class . '@index');
    Route::patch('/admin/helpers/{id}/approve', App\Http\Controllers\HelperController::class . '@approve');
    Route::patch('/admin/helpers/{id}/reject', App\Http\Controllers\HelperController::class . '@reject');

    // Admin KYC Review
    Route::patch('/admin/kyc/{id}/approve', App\Http\Controllers\KYCController::class . '@approve');
    Route::patch('/admin/kyc/{id}/reject', App\Http\Controllers\KYCController::class . '@reject');

    // Admin Withdrawal Management
    Route::get('/admin/withdrawals', App\Http\Controllers\AdminWithdrawalController::class . '@index');
    Route::patch('/admin/withdrawals/{id}/process', App\Http\Controllers\AdminWithdrawalController::class . '@process');
    Route::patch('/admin/withdrawals/{id}/reject', App\Http\Controllers\AdminWithdrawalController::class . '@reject');

    // Admin Dispute Management
    Route::get('/admin/disputes', App\Http\Controllers\DisputeController::class . '@index');
    Route::get('/admin/disputes/{id}', App\Http\Controllers\DisputeController::class . '@show');
    Route::patch('/admin/disputes/{id}/resolve', App\Http\Controllers\DisputeController::class . '@resolve');
});

// Public Routes (no authentication required)
Route::get('/categories', App\Http\Controllers\CategoryController::class . '@index');
Route::get('/categories/{id}', App\Http\Controllers\CategoryController::class . '@show');
Route::get('/helpers/nearby', App\Http\Controllers\AvailabilityController::class . '@nearby');
