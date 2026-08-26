package com.marketplace.onehour.common.network

/**
 * Fallback origin used only when real GPS is unavailable — permission
 * denied, location services off, or no fix yet. See LocationProvider for
 * the real device-location path.
 */
object LocationDefaults {
    const val LAT = 12.9784
    const val LNG = 77.6408
    const val FALLBACK_LABEL = "Indiranagar, Bengaluru"
}
