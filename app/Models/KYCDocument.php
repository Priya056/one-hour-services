<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Hidden;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

#[Hidden(['helper_id', 'reviewed_by'])]
class KYCDocument extends Model
{
    use HasFactory;

    protected $fillable = [
        'helper_id',
        'document_type',
        'document_url',
        'status',
        'reviewed_by',
        'reviewed_at',
    ];

    protected $casts = [
        'reviewed_at' => 'datetime',
    ];

    /**
     * Relationship: Helper Profile
     */
    public function helper(): BelongsTo
    {
        return $this->belongsTo(HelperProfile::class, 'helper_id');
    }

    /**
     * Relationship: Reviewer (User)
     */
    public function reviewer(): BelongsTo
    {
        return $this->belongsTo(User::class, 'reviewed_by');
    }
}
