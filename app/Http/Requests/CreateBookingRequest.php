<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\ValidationException;

class CreateBookingRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return $this->user()->isCustomer();
    }

    /**
     * Get the validation rules that apply to the request.
     */
    public function rules(): array
    {
        return [
            'helper_id' => ['required', 'exists:helper_profiles,id'],
            'category_id' => ['required', 'exists:categories,id'],
            'scheduled_time' => ['required', 'date', 'after:now'],
            'duration_hours' => ['required', 'numeric', 'min:0.5', 'max:8'],
            'location_lat' => ['required', 'numeric', 'between:-90,90'],
            'location_lng' => ['required', 'numeric', 'between:-180,180'],
            'address_text' => ['required', 'string', 'max:500'],
        ];
    }

    /**
     * Configure the validator instance.
     */
    public function withValidator($validator)
    {
        $validator->after(function ($validator) {
            $this->validateHelperAvailability();
            $this->validateHelperEligibility();
            $this->validateHelperService();
            $this->validateNoOverlappingBooking();
        });
    }

    /**
     * Validate helper is available at the scheduled time
     */
    protected function validateHelperAvailability()
    {
        $helper = \App\Models\HelperProfile::find($this->helper_id);
        $scheduledTime = \Carbon\Carbon::parse($this->scheduled_time);

        if (!$helper) {
            return;
        }

        $booking = new \App\Models\Booking();
        if (!$booking->isHelperAvailableAtTime($helper, $scheduledTime)) {
            throw ValidationException::withMessages([
                'scheduled_time' => 'Helper is not available at the scheduled time.',
            ]);
        }
    }

    /**
     * Validate helper eligibility (KYC, active, available now)
     */
    protected function validateHelperEligibility()
    {
        $helper = \App\Models\HelperProfile::find($this->helper_id);

        if (!$helper) {
            return;
        }

        $booking = new \App\Models\Booking();
        if (!$booking->isHelperEligible($helper)) {
            throw ValidationException::withMessages([
                'helper_id' => 'Helper is not eligible for booking (KYC not approved, inactive, or not available).',
            ]);
        }
    }

    /**
     * Validate helper has active service for the category
     */
    protected function validateHelperService()
    {
        $helper = \App\Models\HelperProfile::find($this->helper_id);

        if (!$helper) {
            return;
        }

        $service = \App\Models\HelperService::where('helper_id', $helper->id)
            ->where('category_id', $this->category_id)
            ->where('is_active', true)
            ->first();

        if (!$service) {
            throw ValidationException::withMessages([
                'category_id' => 'Helper does not offer this service.',
            ]);
        }
    }

    /**
     * Validate no overlapping booking exists
     */
    protected function validateNoOverlappingBooking()
    {
        $helper = \App\Models\HelperProfile::find($this->helper_id);
        $scheduledTime = \Carbon\Carbon::parse($this->scheduled_time);
        $durationHours = (float) $this->duration_hours;

        if (!$helper) {
            return;
        }

        $booking = new \App\Models\Booking();
        if ($booking->hasOverlappingBooking($helper, $scheduledTime, $durationHours)) {
            throw ValidationException::withMessages([
                'scheduled_time' => 'Helper already has a booking during this time slot.',
            ]);
        }
    }
}
