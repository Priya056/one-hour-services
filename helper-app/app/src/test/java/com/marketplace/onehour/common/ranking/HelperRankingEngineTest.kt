package com.marketplace.onehour.common.ranking

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HelperRankingEngineTest {

    @Test
    fun `empty list ranks to empty list`() {
        assertEquals(emptyList<RankedHelper>(), HelperRankingEngine.rankHelpers(emptyList()))
    }

    @Test
    fun `single helper gets full distance score regardless of raw distance`() {
        val helper = RankableHelper("h1", "Solo", rating = 4.0, distanceKm = 12.0, responseRatePercent = 80.0, isAvailableNow = true, completedBookings = 10)
        val ranked = HelperRankingEngine.rankHelpers(listOf(helper))
        assertEquals(1, ranked.size)
        assertEquals(1.0, ranked[0].distanceScore, 0.0001)
    }

    @Test
    fun `weights sum to 1`() {
        val total = HelperRankingEngine.WEIGHT_RATING + HelperRankingEngine.WEIGHT_DISTANCE +
            HelperRankingEngine.WEIGHT_RESPONSE + HelperRankingEngine.WEIGHT_AVAILABILITY +
            HelperRankingEngine.WEIGHT_BOOKINGS
        assertEquals(1.0, total, 0.0001)
    }

    @Test
    fun `sorted descending by final score`() {
        val ranked = HelperRankingEngine.rankHelpers(HelperRankingEngine.sampleMockHelpers)
        val scores = ranked.map { it.finalScore }
        assertEquals(scores.sortedDescending(), scores)
    }

    @Test
    fun `a much closer helper can outrank a farther one with a perfect record`() {
        // Regression guard for the actual motivation of this engine: pure
        // distance-only sorting used to bury a great, available, nearby
        // helper behind a slightly-farther one with a marginally higher
        // rating. Sarah (h2) is close, highly rated, and highly responsive;
        // Marcus (h3) is 3x farther despite a perfect rating.
        val ranked = HelperRankingEngine.rankHelpers(HelperRankingEngine.sampleMockHelpers)
        val sarahRank = ranked.indexOfFirst { it.helper.id == "h2" }
        val marcusRank = ranked.indexOfFirst { it.helper.id == "h3" }
        assertTrue("Expected Sarah (closer, near-perfect record) to rank above Marcus (farther)", sarahRank < marcusRank)
    }
}
