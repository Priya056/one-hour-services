package com.marketplace.onehour.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    // Customer Nav Items
    object Home : BottomNavItem("customer_home", "Home", Icons.Default.Home)
    object Bookings : BottomNavItem("booking_history", "Bookings", Icons.Default.History)
    object Settings : BottomNavItem("customer_settings", "Profile", Icons.Default.Person)

    // Helper Nav Items
    object HelperHome : BottomNavItem("helper_home", "Jobs", Icons.Default.Work)
    object Earnings : BottomNavItem("earnings_dashboard", "Earnings", Icons.Default.History)
    object HelperSettings : BottomNavItem("helper_settings", "Profile", Icons.Default.Person)
}

@Composable
fun CustomerBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Bookings,
        BottomNavItem.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}

@Composable
fun HelperBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.HelperHome,
        BottomNavItem.Earnings,
        BottomNavItem.HelperSettings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
