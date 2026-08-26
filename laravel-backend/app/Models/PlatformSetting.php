<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PlatformSetting extends Model
{
    use HasFactory;

    protected $fillable = [
        'key',
        'value',
        'description',
    ];

    /**
     * Get a setting value by key
     */
    public static function getValue(string $key, $default = null)
    {
        $setting = self::where('key', $key)->first();
        return $setting ? $setting->value : $default;
    }

    /**
     * Get commission percentage
     */
    public static function getCommissionPercent(): float
    {
        return (float) self::getValue('default_commission_percent', 15.00);
    }

    /**
     * Get cancellation window in minutes
     */
    public static function getCancellationWindowMinutes(): int
    {
        return (int) self::getValue('booking_cancellation_window_mins', 15);
    }

    /**
     * Get max search radius in km
     */
    public static function getMaxSearchRadiusKm(): float
    {
        return (float) self::getValue('max_search_radius_km', 25.00);
    }
}
