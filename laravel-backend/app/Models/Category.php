<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;

class Category extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'name',
        'icon_url',
        'description',
    ];

    /**
     * Relationship: Helper Services
     */
    public function helperServices(): HasMany
    {
        return $this->hasMany(HelperService::class, 'category_id');
    }
}
