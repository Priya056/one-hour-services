package com.marketplace.onehour.integration.maps

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * Reusable MapView Composable displaying user's current location pin
 * + mock helper pins around Hyderabad.
 */
@Composable
fun MapViewComposable(
    modifier: Modifier = Modifier,
    helpers: List<MockHelperLocation> = MockHelperLocations.hyderabadHelpers,
    onHelperSelected: (MockHelperLocation) -> Unit = {}
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    var userLatLng by remember {
        mutableStateOf(LatLng(LocationHelper.DEFAULT_HYDERABAD_LAT, LocationHelper.DEFAULT_HYDERABAD_LNG))
    }
    var hasPermission by remember { mutableStateOf(locationHelper.hasLocationPermission()) }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = granted
        if (granted) {
            locationHelper.getCurrentLocation(
                onSuccess = { lat, lng -> userLatLng = LatLng(lat, lng) },
                onPermissionDeniedOrFailed = { permissionDenied = true }
            )
        } else {
            permissionDenied = true
        }
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            locationHelper.getCurrentLocation(
                onSuccess = { lat, lng -> userLatLng = LatLng(lat, lng) },
                onPermissionDeniedOrFailed = { /* Use default Hyderabad location */ }
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLatLng, 13f)
    }

    LaunchedEffect(userLatLng) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, 13f)
    }

    Box(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = hasPermission
            )
        ) {
            // Current User Location Marker (Blue pin)
            Marker(
                state = MarkerState(position = userLatLng),
                title = "Your Location",
                snippet = "Current User Location",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )

            // Mock Helper Pins (Red pins around Hyderabad)
            // TODO: Replace mock helper list with response from real GET /api/helpers/nearby endpoint
            helpers.forEach { helper ->
                Marker(
                    state = MarkerState(position = LatLng(helper.latitude, helper.longitude)),
                    title = "${helper.name} (${helper.category})",
                    snippet = "Rating: ⭐ ${helper.rating}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    onClick = {
                        onHelperSelected(helper)
                        false
                    }
                )
            }
        }

        if (permissionDenied) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Location permission denied. Displaying default Hyderabad map view.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
