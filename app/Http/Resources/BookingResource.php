<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class BookingResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'customer_id' => $this->customer_id,
            'helper_id' => $this->helper_id,
            'category_id' => $this->category_id,
            'scheduled_time' => $this->scheduled_time,
            'duration_hours' => (float) $this->duration_hours,
            'status' => $this->status,
            'location_lat' => (float) $this->location_lat,
            'location_lng' => (float) $this->location_lng,
            'address_text' => $this->address_text,
            'total_amount' => (float) $this->total_amount,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
            
            // Relationships
            'customer' => new UserResource($this->whenLoaded('customer')),
            'helper' => new HelperProfileResource($this->whenLoaded('helper')),
            'category' => new CategoryResource($this->whenLoaded('category')),
            'payment' => new PaymentResource($this->whenLoaded('payment')),
            'review' => new ReviewResource($this->whenLoaded('review')),
        ];
    }
}
