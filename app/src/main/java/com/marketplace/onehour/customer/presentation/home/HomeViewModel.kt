package com.marketplace.onehour.customer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.HelperDto
import com.marketplace.onehour.common.network.HelperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Category icon/color assignment is cosmetic only (backend doesn't store
// either) — keyed by name rather than list position so it stays correct
// even if categories get reordered or new ones are added on the backend.
private val CATEGORY_ICONS = mapOf(
    "Personal Assistance" to "Person",
    "Electrical" to "Bolt",
    "Tutoring" to "School",
    "Photography" to "Camera",
    "Home Repairs" to "Build",
    "Errands & Delivery" to "DirectionsRun",
    "Design/Creative" to "Palette",
    "Business/Professional" to "BusinessCenter"
)
private val CATEGORY_COLORS = mapOf(
    "Personal Assistance" to 0xFF7C3AED,
    "Electrical" to 0xFF2563EB,
    "Tutoring" to 0xFF10B981,
    "Photography" to 0xFFF59E0B,
    "Home Repairs" to 0xFFEF4444,
    "Errands & Delivery" to 0xFF8B5CF6,
    "Design/Creative" to 0xFFEC4899,
    "Business/Professional" to 0xFF06B6D4
)
private const val DEFAULT_CATEGORY_ICON = "Person"
private const val DEFAULT_CATEGORY_COLOR = 0xFF7C3AED

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private var allFetchedHelpers: List<HelperDto> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val categoriesResponse = ApiClient.api.getCategories()
                val categories = categoriesResponse.data.map { dto ->
                    CategoryItem(
                        id = dto.id.toString(),
                        name = dto.name,
                        iconName = CATEGORY_ICONS[dto.name] ?: DEFAULT_CATEGORY_ICON,
                        colorHex = CATEGORY_COLORS[dto.name] ?: DEFAULT_CATEGORY_COLOR
                    )
                }
                _uiState.value = _uiState.value.copy(categories = categories)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Couldn't load categories: ${e.message}")
            }

            fetchNearbyHelpers()
        }
    }

    /** Called once the Composable resolves a real device location (or the fallback). */
    fun setLocation(lat: Double, lng: Double, label: String) {
        _uiState.value = _uiState.value.copy(userLat = lat, userLng = lng, userLocation = label)
        viewModelScope.launch {
            fetchNearbyHelpers(categoryId = _uiState.value.selectedCategory?.toIntOrNull())
        }
    }

    private suspend fun fetchNearbyHelpers(categoryId: Int? = null, maxDistanceKm: Double? = null) {
        try {
            val response = ApiClient.api.getNearbyHelpersRaw(
                lat = _uiState.value.userLat,
                lng = _uiState.value.userLng,
                categoryId = categoryId,
                maxDistanceKm = maxDistanceKm
            )
            val mapped = response.data.map { dto ->
                HelperDto(
                    id = dto.id.toString(),
                    name = dto.name,
                    photoUrl = dto.profilePhotoUrl ?: "",
                    mainCategory = dto.category?.name ?: "",
                    categoryId = dto.category?.id?.toString() ?: "",
                    rating = dto.averageRating,
                    reviewCount = dto.totalReviews,
                    hourlyRate = dto.hourlyRate ?: 0.0,
                    distanceKm = dto.distanceKm,
                    bio = dto.bio ?: "",
                    skills = emptyList(),
                    isAvailable = true // /helpers/nearby only ever returns currently-available helpers
                )
            }
            allFetchedHelpers = mapped
            HelperRepository.store(mapped)
            _uiState.value = _uiState.value.copy(nearbyHelpers = mapped, isLoading = false, errorMessage = null)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Couldn't load nearby helpers: ${e.message}")
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterHelpers()
    }

    fun onCategorySelected(categoryId: String?) {
        val newCategory = if (_uiState.value.selectedCategory == categoryId) null else categoryId
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
        viewModelScope.launch {
            fetchNearbyHelpers(categoryId = newCategory?.toIntOrNull())
        }
    }

    fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(isMapView = !_uiState.value.isMapView)
    }

    fun applyAdvancedFilters(
        maxPrice: Float = 100f,
        minRating: Float = 0f,
        maxDistanceKm: Float = 25f,
        availableNowOnly: Boolean = false
    ) {
        val query = _uiState.value.searchQuery.lowercase()

        // Distance is re-queried server-side; price/rating/text/availability are
        // applied client-side over that result since the endpoint doesn't take them.
        viewModelScope.launch {
            fetchNearbyHelpers(
                categoryId = _uiState.value.selectedCategory?.toIntOrNull(),
                maxDistanceKm = maxDistanceKm.toDouble()
            )

            val filtered = allFetchedHelpers.filter { helper ->
                val matchesQuery = query.isEmpty() ||
                        helper.name.lowercase().contains(query) ||
                        helper.mainCategory.lowercase().contains(query)

                val matchesPrice = helper.hourlyRate <= maxPrice
                val matchesRating = helper.rating >= minRating
                val matchesAvailability = !availableNowOnly || helper.isAvailable

                matchesQuery && matchesPrice && matchesRating && matchesAvailability
            }

            _uiState.value = _uiState.value.copy(nearbyHelpers = filtered)
        }
    }

    private fun filterHelpers() {
        val query = _uiState.value.searchQuery.lowercase()
        val filtered = allFetchedHelpers.filter { helper ->
            query.isEmpty() || helper.name.lowercase().contains(query) || helper.mainCategory.lowercase().contains(query)
        }
        _uiState.value = _uiState.value.copy(nearbyHelpers = filtered)
    }
}
