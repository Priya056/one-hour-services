package com.marketplace.onehour.common.network

/**
 * In-memory cache of the helpers last fetched from GET /helpers/nearby.
 * The backend has no standalone "get helper by id" endpoint, so downstream
 * screens (profile, booking, chat, tracking, review) look a helper up here
 * instead of re-fetching — populated by HomeViewModel after each search.
 */
object HelperRepository {
    private var helpersById: Map<String, HelperDto> = emptyMap()

    fun store(helpers: List<HelperDto>) {
        helpersById = helpers.associateBy { it.id }
    }

    fun findById(id: String): HelperDto? = helpersById[id]

    fun all(): List<HelperDto> = helpersById.values.toList()
}
