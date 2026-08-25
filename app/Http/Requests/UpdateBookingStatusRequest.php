<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\ValidationException;

class UpdateBookingStatusRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true; // Authorization handled in controller/policy
    }

    /**
     * Get the validation rules that apply to the request.
     */
    public function rules(): array
    {
        return [
            'status' => ['required', 'in:requested,accepted,rejected,on_the_way,in_progress,completed,cancelled'],
        ];
    }

    /**
     * Configure the validator instance.
     */
    public function withValidator($validator)
    {
        $validator->after(function ($validator) {
            $bookingId = $this->route('id');
            $booking = \App\Models\Booking::find($bookingId);
            
            if ($booking && !$booking->canTransitionTo($this->status)) {
                throw ValidationException::withMessages([
                    'status' => "Cannot transition from {$booking->status} to {$this->status}.",
                ]);
            }
        });
    }
}
