package com.marketplace.onehour.common.placeholders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.marketplace.onehour.integration.maps.MapViewComposable

/**
 * Integration point for Google Maps SDK.
 * Renders the modular MapViewComposable showing current location + mock helper pins around Hyderabad.
 */
@Composable
fun MapsPlaceholder(
    modifier: Modifier = Modifier,
    label: String = "Interactive Helper Map"
) {
    // Renders the live Google Map view with mock helper pins
    MapViewComposable(modifier = modifier)
}
