<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    protected $fillable = [
        'name',
        'phone',
        'email',
        'password_hash',
        'role',
        'profile_photo_url',
        'address',
        'is_active'
    ];

    public function helperProfile()
    {
        return $this->hasOne(HelperProfile::class);
    }

    public function bookings()
    {
        return $this->hasMany(Booking::class, 'customer_id');
    }
}
