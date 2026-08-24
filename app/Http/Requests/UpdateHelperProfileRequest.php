<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class UpdateHelperProfileRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     */
    public function rules(): array
    {
        return [
            'bio' => ['sometimes', 'string'],
            'experience_years' => ['sometimes', 'integer', 'min:0'],
            'service_radius_km' => ['sometimes', 'numeric', 'min:0', 'max:100'],
        ];
    }
}
