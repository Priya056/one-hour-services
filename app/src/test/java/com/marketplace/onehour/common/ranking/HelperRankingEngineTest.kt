package com.marketplace.onehour.common.ranking

import org.junit.Test

class HelperRankingEngineTest {

    @Test
    fun printComparison() {
        val mockData = HelperRankingEngine.sampleMockHelpers
        val distanceSorted = mockData.sortedBy { it.distanceKm }
        val smartRanked = HelperRankingEngine.rankHelpers(mockData)

        println("=== OLD ORDER (Distance-Only Sort) ===")
        distanceSorted.forEachIndexed { index, h ->
            println("${index + 1}. ${h.name} | Dist: ${h.distanceKm}km | Rating: ${h.rating} | Response: ${h.responseRatePercent.toInt()}% | Avail: ${if (h.isAvailableNow) "Yes" else "No"} | Bookings: ${h.completedBookings}")
        }

        println("\n=== NEW ORDER (Smart Multi-Factor Ranking) ===")
        smartRanked.forEachIndexed { index, rh ->
            val h = rh.helper
            val scorePct = String.format("%.2f", rh.finalScore * 100)
            println("${index + 1}. ${h.name} (Score: $scorePct%) | Dist: ${h.distanceKm}km | Rating: ${h.rating} | Response: ${h.responseRatePercent.toInt()}% | Avail: ${if (h.isAvailableNow) "Yes" else "No"} | Bookings: ${h.completedBookings}")
        }
    }
}
