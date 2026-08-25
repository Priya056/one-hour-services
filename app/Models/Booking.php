<?php

namespace App\Models;

use App\Services\AvailabilityService;
use Carbon\Carbon;
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
     * Valid status transitions
     */
    protected static $statusTransitions = [
        'requested' => ['accepted', 'rejected', 'cancelled'],
        'accepted' => ['on_the_way', 'cancelled'],
        'on_the_way' => ['in_progress', 'cancelled'],
        'in_progress' => ['completed', 'cancelled'],
        'completed' => [],
        'rejected' => [],
        'cancelled' => [],
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

    /**
     * Check if status transition is valid
     */
    public function canTransitionTo(string $newStatus): bool
    {
        return in_array($newStatus, self::$statusTransitions[$this->status] ?? []);
    }

    /**
     * Check if helper is eligible for booking
     */
    public function isHelperEligible(HelperProfile $helper): bool
    {
        // Helper must have KYC approved
        if ($helper->kyc_status !== 'approved') {
            return false;
        }

        // Helper must be active
        if (!$helper->user->is_active) {
            return false;
        }

        // Helper must be available now
        if (!$helper->is_available_now) {
            return false;
        }

        return true;
    }

    /**
     * Check if helper is available at the scheduled time
     */
    public function isHelperAvailableAtTime(HelperProfile $helper, Carbon $scheduledTime): bool
    {
        $availabilityService = app(AvailabilityService::class);
        return $availabilityService->isAvailableAt($helper, $scheduledTime);
    }

    /**
     * Check for overlapping bookings for the helper
     */
    public function hasOverlappingBooking(HelperProfile $helper, Carbon $scheduledTime, float $durationHours): bool
    {
        $startTime = $scheduledTime;
        $endTime = $scheduledTime->copy()->addHours((float) $durationHours);

        $existingBookings = self::where('helper_id', $helper->id)
            ->where('id', '!=', $this->id ?? 0)
            ->whereIn('status', ['requested', 'accepted', 'on_the_way', 'in_progress'])
            ->get();

        foreach ($existingBookings as $existing) {
            $existingStart = $existing->scheduled_time;
            $existingEnd = $existing->scheduled_time->copy()->addHours((float) $existing->duration_hours);

            // Check for overlap
            if ($startTime < $existingEnd && $endTime > $existingStart) {
                return true;
            }
        }

        return false;
    }

    /**
     * Scope: Bookings for a customer
     */
    public function scopeForCustomer($query, $customerId)
    {
        return $query->where('customer_id', $customerId);
    }

    /**
     * Scope: Bookings for a helper
     */
    public function scopeForHelper($query, $helperId)
    {
        return $query->where('helper_id', $helperId);
    }

    /**
     * Scope: Bookings with a specific status
     */
    public function scopeWithStatus($query, $status)
    {
        return $query->where('status', $status);
    }

    /**
     * Scope: Upcoming bookings
     */
    public function scopeUpcoming($query)
    {
        return $query->where('scheduled_time', '>', now())
                    ->whereIn('status', ['requested', 'accepted', 'on_the_way']);
    }

    /**
     * Scope: Past bookings
     */
    public function scopePast($query)
    {
        return $query->where('scheduled_time', '<', now())
                    ->whereIn('status', ['completed', 'cancelled', 'rejected']);
    }
}
