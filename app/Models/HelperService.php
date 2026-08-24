<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Attributes\Hidden;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\SoftDeletes;

#[Hidden(['helper_id'])]
class HelperService extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'helper_id',
        'category_id',
        'hourly_rate',
        'is_active',
    ];

    protected $casts = [
        'hourly_rate' => 'decimal:2',
        'is_active' => 'boolean',
    ];

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
}
