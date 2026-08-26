package com.marketplace.onehour.integration.maps

/**
 * Data class representing a helper's geographical location and service summary.
 */
data class MockHelperLocation(
    val id: String,
    val name: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double
)

object MockHelperLocations {
    /**
     * 5-6 mock helper locations situated around Hyderabad coordinates.
     */
    val hyderabadHelpers = listOf(
        MockHelperLocation("h1", "Rajesh Kumar", "Electrician", 17.3850, 78.4867, 4.8),
        MockHelperLocation("h2", "Suresh Varma", "Plumber", 17.4065, 78.4772, 4.7),
        MockHelperLocation("h3", "Priya Sharma", "Cleaning", 17.4401, 78.3489, 4.9), // Gachibowli
        MockHelperLocation("h4", "Amit Patel", "AC Repair", 17.4399, 78.4483, 4.6),  // Begumpet
        MockHelperLocation("h5", "Vikram Reddy", "Carpenter", 17.4239, 78.4738, 4.8), // Lakdikapul
        MockHelperLocation("h6", "Mohammed Ali", "Painter", 17.3616, 78.4747, 4.9)  // Charminar
    )

    // TODO: Connect this mock list to the real GET /api/helpers/nearby backend endpoint later
}
