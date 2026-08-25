<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class PaymentResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->id,
            'amount' => (float) $this->amount,
            'platform_commission' => (float) $this->platform_commission,
            'helper_payout_amount' => (float) $this->helper_payout_amount,
            'payment_gateway' => $this->payment_gateway,
            'gateway_transaction_id' => $this->gateway_transaction_id,
            'status' => $this->status,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}
