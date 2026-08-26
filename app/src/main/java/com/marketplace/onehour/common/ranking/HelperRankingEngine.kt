package com.marketplace.onehour.common.ranking

import kotlin.math.ln
import kotlin.math.min

/**
 * Data representation of a helper with raw performance & location metrics
 * used for calculating the smart ranking relevance score.
 */
data class RankableHelper(
    val id: String,
    val name: String,
    val rating: Double,             // 0.0 to 5.0 stars
    val distanceKm: Double,         // Distance from customer in km
    val responseRatePercent: Double,// Percentage of accepted requests (0.0 to 100.0)
    val isAvailableNow: Boolean,    // Availability status
    val completedBookings: Int      // Total completed jobs/reviews
)

/**
 * Result wrapper holding the original helper data alongside its calculated score
 * and individual factor breakdowns.
 */
data class RankedHelper(
    val helper: RankableHelper,
    val finalScore: Double,
    val ratingScore: Double,
    val distanceScore: Double,
    val responseScore: Double,
    val availabilityScore: Double,
    val bookingsScore: Double
)

/**
 * HelperRankingEngine: Multi-factor scoring engine for Lumina 1-Hour Marketplace.
 *
 * TODO: BACKEND MIGRATION NOTICE
 * ------------------------------
 * When real booking history and analytics backend services are available, this entire engine
 * should be migrated to the backend (e.g., Laravel service / Microservice).
 * Server-side ranking allows querying and filtering directly against PostgreSQL/MySQL/Elasticsearch,
 * using cached metrics (response rates, completion counts) without fetching raw data to the client device.
 * Backend Endpoint Target: GET /api/v1/helpers/nearby?lat=...&lng=...&sort=smart_rank
 */
object HelperRankingEngine {

    // --- WEIGHT CONFIGURATION (Sum = 1.0) ---
    // 1. Rating (40% weight) - Higher customer rating boosts rank
    const val WEIGHT_RATING = 0.40

    // 2. Distance (25% weight) - Closer proximity gets higher rank
    const val WEIGHT_DISTANCE = 0.25

    // 3. Response Rate (20% weight) - Quick acceptance rate improves rank
    const val WEIGHT_RESPONSE = 0.20

    // 4. Availability Status (10% weight) - Currently available boosted over offline/later
    const val WEIGHT_AVAILABILITY = 0.10

    // 5. Completed Bookings (5% weight) - More job history equals higher trust (with diminishing returns)
    const val WEIGHT_BOOKINGS = 0.05

    // Saturation target for logarithmic scaling of completed bookings (diminishing returns threshold)
    private const val BOOKINGS_SATURATION_TARGET = 100.0

    /**
     * Scores and ranks a list of helpers based on weighted relevance criteria.
     *
     * @param helpers List of nearby [RankableHelper] items to rank.
     * @return List of [RankedHelper] sorted descending by finalScore.
     */
    fun rankHelpers(helpers: List<RankableHelper>): List<RankedHelper> {
        if (helpers.isEmpty()) return emptyList()

        val minDist = helpers.minOf { it.distanceKm }
        val maxDist = helpers.maxOf { it.distanceKm }

        return helpers.map { helper ->
            // Factor 1: Rating (0.0 to 5.0 -> 0.0 to 1.0)
            val normRating = (helper.rating / 5.0).coerceIn(0.0, 1.0)

            // Factor 2: Distance (Relative min-max inverse normalization)
            val normDistance = if (maxDist > minDist) {
                (1.0 - ((helper.distanceKm - minDist) / (maxDist - minDist))).coerceIn(0.0, 1.0)
            } else {
                1.0
            }

            // Factor 3: Response Rate (0.0% to 100.0% -> 0.0 to 1.0)
            val normResponse = (helper.responseRatePercent / 100.0).coerceIn(0.0, 1.0)

            // Factor 4: Availability (Available Now = 1.0, Offline/Later = 0.0)
            val normAvailability = if (helper.isAvailableNow) 1.0 else 0.0

            // Factor 5: Completed Bookings (Logarithmic saturation: diminishing returns)
            val normBookings = min(
                1.0,
                ln(1.0 + helper.completedBookings) / ln(1.0 + BOOKINGS_SATURATION_TARGET)
            ).coerceIn(0.0, 1.0)

            // Calculate total weighted score
            val totalScore = (normRating * WEIGHT_RATING) +
                    (normDistance * WEIGHT_DISTANCE) +
                    (normResponse * WEIGHT_RESPONSE) +
                    (normAvailability * WEIGHT_AVAILABILITY) +
                    (normBookings * WEIGHT_BOOKINGS)

            RankedHelper(
                helper = helper,
                finalScore = totalScore,
                ratingScore = normRating,
                distanceScore = normDistance,
                responseScore = normResponse,
                availabilityScore = normAvailability,
                bookingsScore = normBookings
            )
        }.sortedByDescending { it.finalScore }
    }

    /**
     * Sample mock dataset containing 9 varied helper profiles for testing smart ranking logic.
     */
    val sampleMockHelpers = listOf(
        RankableHelper(
            id = "h1",
            name = "Alex Rivera",
            rating = 3.2,
            distanceKm = 0.3,
            responseRatePercent = 60.0,
            isAvailableNow = true,
            completedBookings = 12
        ),
        RankableHelper(
            id = "h2",
            name = "Sarah Jenkins",
            rating = 4.9,
            distanceKm = 1.1,
            responseRatePercent = 98.0,
            isAvailableNow = true,
            completedBookings = 140
        ),
        RankableHelper(
            id = "h3",
            name = "Marcus Vance",
            rating = 5.0,
            distanceKm = 3.5,
            responseRatePercent = 100.0,
            isAvailableNow = true,
            completedBookings = 85
        ),
        RankableHelper(
            id = "h4",
            name = "David Chen",
            rating = 4.8,
            distanceKm = 0.6,
            responseRatePercent = 45.0,
            isAvailableNow = true,
            completedBookings = 30
        ),
        RankableHelper(
            id = "h5",
            name = "Elena Rostova",
            rating = 4.9,
            distanceKm = 1.5,
            responseRatePercent = 95.0,
            isAvailableNow = false,
            completedBookings = 450
        ),
        RankableHelper(
            id = "h6",
            name = "James Wilson",
            rating = 4.7,
            distanceKm = 0.9,
            responseRatePercent = 90.0,
            isAvailableNow = true,
            completedBookings = 3
        ),
        RankableHelper(
            id = "h7",
            name = "Priya Sharma",
            rating = 4.1,
            distanceKm = 2.0,
            responseRatePercent = 80.0,
            isAvailableNow = true,
            completedBookings = 25
        ),
        RankableHelper(
            id = "h8",
            name = "Tom Hardy",
            rating = 4.0,
            distanceKm = 4.2,
            responseRatePercent = 50.0,
            isAvailableNow = true,
            completedBookings = 60
        ),
        RankableHelper(
            id = "h9",
            name = "Anita Roy",
            rating = 4.9,
            distanceKm = 5.0,
            responseRatePercent = 96.0,
            isAvailableNow = true,
            completedBookings = 210
        )
    )
}
