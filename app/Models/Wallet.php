<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Hidden;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

#[Hidden(['helper_id'])]
class Wallet extends Model
{
    use HasFactory;

    protected $fillable = [
        'helper_id',
        'balance',
        'last_updated_at',
    ];

    protected $casts = [
        'balance' => 'decimal:2',
        'last_updated_at' => 'datetime',
    ];

    /**
     * Relationship: Helper Profile
     */
    public function helper(): BelongsTo
    {
        return $this->belongsTo(HelperProfile::class, 'helper_id');
    }

    /**
     * Relationship: Wallet Transactions
     */
    public function transactions(): HasMany
    {
        return $this->hasMany(WalletTransaction::class, 'wallet_id');
    }
}
