package com.marketplace.onehour.common.placeholders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Placeholder integration point for Google Maps SDK.
 * TODO: Replace this composable with com.google.maps.android.compose.GoogleMap when API key is configured.
 */
@Composable
fun MapsPlaceholder(
    modifier: Modifier = Modifier,
    label: String = "Interactive Helper Map (Google Maps SDK Integration Point)"
) {
    Box(
        modifier = modifier
            .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = "Map Placeholder",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "TODO: Google Maps SDK Key & Realtime Coordinates Sync",
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
