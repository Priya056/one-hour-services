<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Booking extends Model
{
    protected $fillable = [
        'customer_id',
        'helper_id',
        'category_id',
        'scheduled_time',
        'duration_hours',
        'status',
        'location_lat',
        'location_lng',
        'address_text',
        'total_amount'
    ];

    public function customer()
    {
        return $this->belongsTo(User::class, 'customer_id');
    }

    public function helper()
    {
        return $this->belongsTo(HelperProfile::class, 'helper_id');
    }
}
