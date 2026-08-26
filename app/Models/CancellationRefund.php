<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Hidden;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

#[Hidden(['booking_id'])]
class CancellationRefund extends Model
{
    use HasFactory;

    protected $table = 'cancellations_refunds';

    protected $fillable = [
        'booking_id',
        'cancelled_by',
        'cancellation_reason',
        'refund_amount',
        'refund_status',
        'refund_initiated_at',
        'refund_processed_at',
    ];

    protected $casts = [
        'refund_amount' => 'decimal:2',
        'refund_initiated_at' => 'datetime',
        'refund_processed_at' => 'datetime',
    ];

    /**
     * Relationship: Booking
     */
    public function booking(): BelongsTo
    {
        return $this->belongsTo(Booking::class, 'booking_id');
    }

    /**
     * Relationship: User who cancelled
     */
    public function cancelledBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'cancelled_by');
    }
}
