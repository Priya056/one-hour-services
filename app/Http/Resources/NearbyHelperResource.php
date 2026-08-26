<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class NearbyHelperResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     */
    public function toArray(Request $request): array
    {
        $service = $this->helperServices->first();

        return [
            'id' => $this->id,
            'name' => $this->user->name,
            'profile_photo_url' => $this->user->profile_photo_url,
            'bio' => $this->bio,
            'category' => $service ? new CategoryResource($service->category) : null,
            'hourly_rate' => $service?->hourly_rate,
            'average_rating' => $this->average_rating,
            'total_reviews' => $this->total_reviews,
            'distance_km' => round((float) $this->distance_km, 2),
        ];
    }
}
