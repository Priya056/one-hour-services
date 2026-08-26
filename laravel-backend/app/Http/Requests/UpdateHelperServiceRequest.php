<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class UpdateHelperServiceRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return $this->user()->isHelper();
    }

    /**
     * Get the validation rules that apply to the request.
     */
    public function rules(): array
    {
        return [
            'hourly_rate' => ['sometimes', 'numeric', 'min:0', 'max:99999.99'],
            'is_active' => ['sometimes', 'boolean'],
        ];
    }
}
