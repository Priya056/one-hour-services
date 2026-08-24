<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasOne;

class Booking extends Model
{
    use HasFactory;

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
        'total_amount',
    ];

    protected $casts = [
        'scheduled_time' => 'datetime',
        'duration_hours' => 'decimal:2',
        'location_lat' => 'decimal:8',
        'location_lng' => 'decimal:8',
        'total_amount' => 'decimal:2',
    ];

    /**
     * Relationship: Customer (User)
     */
    public function customer(): BelongsTo
    {
        return $this->belongsTo(User::class, 'customer_id');
    }

    /**
     * Relationship: Helper Profile
     */
    public function helper(): BelongsTo
    {
        return $this->belongsTo(HelperProfile::class, 'helper_id');
    }

    /**
     * Relationship: Category
     */
    public function category(): BelongsTo
    {
        return $this->belongsTo(Category::class, 'category_id');
    }

    /**
     * Relationship: Payment
     */
    public function payment(): HasOne
    {
        return $this->hasOne(Payment::class, 'booking_id');
    }

    /**
     * Relationship: Review
     */
    public function review(): HasOne
    {
        return $this->hasOne(Review::class, 'booking_id');
    }
}
