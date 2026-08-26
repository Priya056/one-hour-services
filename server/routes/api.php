<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\HelperController;
use App\Http\Controllers\Api\BookingController;

/*
|--------------------------------------------------------------------------
| Lumina 1-Hour Marketplace REST API Routes
|--------------------------------------------------------------------------
*/

Route::prefix('v1')->group(function () {
    Route::get('/helpers/nearby', [HelperController::class, 'index']);
    Route::get('/helpers/{id}', [HelperController::class, 'show']);
    Route::get('/bookings/user/{userId}', [BookingController::class, 'userBookings']);
    Route::post('/bookings', [BookingController::class, 'store']);
});
