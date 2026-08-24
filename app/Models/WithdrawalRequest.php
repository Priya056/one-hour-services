<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Hidden;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

#[Hidden(['helper_id'])]
class WithdrawalRequest extends Model
{
    use HasFactory;

    protected $fillable = [
        'helper_id',
        'amount',
        'bank_account_details',
        'status',
        'requested_at',
        'processed_at',
    ];

    protected $casts = [
        'amount' => 'decimal:2',
        'bank_account_details' => 'array',
        'requested_at' => 'datetime',
        'processed_at' => 'datetime',
    ];

    /**
     * Relationship: Helper Profile
     */
    public function helper(): BelongsTo
    {
        return $this->belongsTo(HelperProfile::class, 'helper_id');
    }
}
