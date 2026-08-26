package com.marketplace.onehour.common.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.marketplace.onehour.common.network.LocationDefaults
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class DeviceLocation(val lat: Double, val lng: Double, val label: String)

/**
 * Wraps FusedLocationProviderClient + Geocoder behind one call. Falls back
 * to LocationDefaults (a fixed Bengaluru coordinate) whenever permission
 * isn't granted or the device can't produce a location — that fallback is
 * honest about being a fallback, not presented as the user's real location.
 */
object LocationProvider {
    private const val FIX_TIMEOUT_MS = 10_000L

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    suspend fun getCurrentLocation(context: Context): DeviceLocation {
        if (!hasPermission(context)) {
            return DeviceLocation(LocationDefaults.LAT, LocationDefaults.LNG, LocationDefaults.FALLBACK_LABEL)
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        // A cached last-known fix answers instantly when one exists; only
        // fall through to a fresh (slower, sometimes-never-resolves-on-an-
        // emulator) request if there truly isn't one yet.
        val location = fetchLastLocation(fusedClient)
            ?: withTimeoutOrNull(FIX_TIMEOUT_MS) { fetchFreshLocation(fusedClient) }
            ?: return DeviceLocation(LocationDefaults.LAT, LocationDefaults.LNG, LocationDefaults.FALLBACK_LABEL)

        val label = reverseGeocode(context, location.latitude, location.longitude)
            ?: LocationDefaults.FALLBACK_LABEL

        return DeviceLocation(location.latitude, location.longitude, label)
    }

    private suspend fun fetchLastLocation(
        fusedClient: com.google.android.gms.location.FusedLocationProviderClient
    ): android.location.Location? = suspendCancellableCoroutine { cont ->
        try {
            fusedClient.lastLocation
                .addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }

    private suspend fun fetchFreshLocation(
        fusedClient: com.google.android.gms.location.FusedLocationProviderClient
    ): android.location.Location? = suspendCancellableCoroutine { cont ->
        try {
            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { loc -> cont.resume(loc) }
                .addOnFailureListener { cont.resume(null) }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }

    private fun reverseGeocode(context: Context, lat: Double, lng: Double): String? = try {
        @Suppress("DEPRECATION")
        val addresses = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lng, 1)
        addresses?.firstOrNull()?.let { addr ->
            listOfNotNull(addr.subLocality ?: addr.locality, addr.adminArea)
                .joinToString(", ")
                .ifBlank { null }
        }
    } catch (e: Exception) {
        null
    }
}
