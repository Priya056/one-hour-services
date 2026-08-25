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
});

// Admin Routes (require admin role)
Route::middleware(['auth:sanctum', 'role:admin'])->group(function () {
    // Admin Helper Management
    Route::get('/admin/helpers', App\Http\Controllers\HelperController::class . '@index');
    Route::patch('/admin/helpers/{id}/approve', App\Http\Controllers\HelperController::class . '@approve');
    Route::patch('/admin/helpers/{id}/reject', App\Http\Controllers\HelperController::class . '@reject');

    // Admin KYC Review
    Route::patch('/admin/kyc/{id}/approve', App\Http\Controllers\KYCController::class . '@approve');
    Route::patch('/admin/kyc/{id}/reject', App\Http\Controllers\KYCController::class . '@reject');
});

// Public Routes (no authentication required)
Route::get('/categories', App\Http\Controllers\CategoryController::class . '@index');
Route::get('/categories/{id}', App\Http\Controllers\CategoryController::class . '@show');
Route::get('/helpers/nearby', App\Http\Controllers\AvailabilityController::class . '@nearby');
