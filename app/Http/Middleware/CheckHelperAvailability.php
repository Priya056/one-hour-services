<?php

namespace App\Http\Middleware;

use App\Services\AvailabilityService;
use Carbon\Carbon;
use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class CheckHelperAvailability
{
    protected AvailabilityService $availabilityService;

    public function __construct(AvailabilityService $availabilityService)
    {
        $this->availabilityService = $availabilityService;
    }

    /**
     * Handle an incoming request to check helper availability before booking.
     *
     * This middleware expects the request to contain helper_id and scheduled_time
     */
    public function handle(Request $request, Closure $next): Response
    {
        $helperId = $request->input('helper_id');
        $scheduledTime = $request->input('scheduled_time');

        if (!$helperId || !$scheduledTime) {
            return response()->json([
                'message' => 'helper_id and scheduled_time are required for availability check.',
            ], 422);
        }

        $helper = \App\Models\HelperProfile::findOrFail($helperId);
        $scheduledDateTime = Carbon::parse($scheduledTime);

        if (!$this->availabilityService->isAvailableAt($helper, $scheduledDateTime)) {
            return response()->json([
                'message' => 'Helper is not available at the requested time.',
                'reason' => 'The helper is either not marked as available, has pending KYC, is inactive, or does not have a schedule for this time slot.',
            ], 400);
        }

        return $next($request);
    }
}
