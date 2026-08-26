<?php

namespace App\Http\Controllers\Api;

use App\Models\Booking;
use Illuminate\Http\Request;

class BookingController
{
    public function userBookings($userId)
    {
        return response()->json([
            [
                "id" => "b101",
                "helperId" => "h1",
                "helperName" => "Alex Rivera",
                "customerId" => $userId,
                "customerName" => "Priya Sharma",
                "serviceName" => "Electrical Repair",
                "status" => "Completed",
                "paymentStatus" => "paid",
                "scheduledTime" => "Today, 12:30 PM",
                "totalAmount" => 35.0,
                "orderId" => "order_lum_9841",
                "paymentId" => "pay_mock_12345"
            ]
        ]);
    }

    public function store(Request $request)
    {
        return response()->json([
            "message" => "Booking created successfully",
            "bookingId" => "b_" . time(),
            "status" => "requested"
        ], 201);
    }
}
