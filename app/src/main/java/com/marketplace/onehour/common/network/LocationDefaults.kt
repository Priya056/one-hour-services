package com.marketplace.onehour.common.network

/**
 * Real device GPS integration is a separate scope item (MapsPlaceholder isn't
 * wired yet). Indiranagar, Bengaluru is used as the search/booking origin
 * everywhere a real coordinate is needed until that lands.
 */
object LocationDefaults {
    const val LAT = 12.9784
    const val LNG = 77.6408
}
