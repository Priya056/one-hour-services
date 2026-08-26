<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class HelperProfile extends Model
{
    protected $fillable = [
        'user_id',
        'bio',
        'experience_years',
        'is_available_now',
        'service_radius_km',
        'average_rating',
        'total_reviews',
        'kyc_status'
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
