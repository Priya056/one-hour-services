<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class HelperProfileResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'bio' => $this->bio,
            'experience_years' => $this->experience_years,
            'is_available_now' => $this->is_available_now,
            'service_radius_km' => $this->service_radius_km,
            'average_rating' => $this->average_rating,
            'total_reviews' => $this->total_reviews,
            'kyc_status' => $this->kyc_status,
            'user' => new UserResource($this->whenLoaded('user')),
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}
