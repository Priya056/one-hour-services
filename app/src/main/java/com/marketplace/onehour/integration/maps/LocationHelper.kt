package com.marketplace.onehour.integration.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Modular Location Helper for requesting location permissions,
 * fetching current user coordinates (lat/lng), and handling permission denials gracefully.
 */
class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Check if location permissions (FINE or COARSE) have been granted.
     */
    fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    /**
     * Fetches current latitude and longitude using FusedLocationProviderClient.
     * Falls back to default Hyderabad location or triggers callback if permission denied/failed.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onSuccess: (latitude: Double, longitude: Double) -> Unit,
        onPermissionDeniedOrFailed: () -> Unit
    ) {
        if (!hasLocationPermission()) {
            onPermissionDeniedOrFailed()
            return
        }

        try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onSuccess(location.latitude, location.longitude)
                } else {
                    // Fallback to last known location if current is null
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                        if (lastLoc != null) {
                            onSuccess(lastLoc.latitude, lastLoc.longitude)
                        } else {
                            onPermissionDeniedOrFailed()
                        }
                    }.addOnFailureListener {
                        onPermissionDeniedOrFailed()
                    }
                }
            }.addOnFailureListener {
                onPermissionDeniedOrFailed()
            }
        } catch (e: Exception) {
            onPermissionDeniedOrFailed()
        }
    }

    companion object {
        // Fallback default coordinates: Hyderabad Center
        const val DEFAULT_HYDERABAD_LAT = 17.3850
        const val DEFAULT_HYDERABAD_LNG = 78.4867
    }
}
