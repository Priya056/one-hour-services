<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Hidden;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

#[Hidden(['user_id'])]
class HelperProfile extends Model
{
    use HasFactory;

    protected $fillable = [
        'user_id',
        'bio',
        'experience_years',
        'is_available_now',
        'service_radius_km',
        'current_lat',
        'current_lng',
        'location_updated_at',
        'average_rating',
        'total_reviews',
        'kyc_status',
    ];

    protected $casts = [
        'is_available_now' => 'boolean',
        'service_radius_km' => 'decimal:2',
        'current_lat' => 'decimal:8',
        'current_lng' => 'decimal:8',
        'location_updated_at' => 'datetime',
        'average_rating' => 'decimal:2',
        'experience_years' => 'integer',
        'total_reviews' => 'integer',
    ];

    /**
     * Relationship: User
     */
    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    /**
     * Relationship: Helper Services
     */
    public function helperServices(): HasMany
    {
        return $this->hasMany(HelperService::class, 'helper_id');
    }

    /**
     * Relationship: Helper Availability
     */
    public function helperAvailability(): HasMany
    {
        return $this->hasMany(HelperAvailability::class, 'helper_id');
    }

    /**
     * Relationship: KYC Documents
     */
    public function kycDocuments(): HasMany
    {
        return $this->hasMany(KYCDocument::class, 'helper_id');
    }

    /**
     * Relationship: Wallet
     */
    public function wallet()
    {
        return $this->hasOne(Wallet::class, 'helper_id');
    }

    /**
     * Relationship: Withdrawal Requests
     */
    public function withdrawalRequests(): HasMany
    {
        return $this->hasMany(WithdrawalRequest::class, 'helper_id');
    }
}
