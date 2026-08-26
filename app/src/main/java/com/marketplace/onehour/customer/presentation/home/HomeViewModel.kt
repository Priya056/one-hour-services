package com.marketplace.onehour.customer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.CategoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadNearbyHelpers()
    }

    private fun loadCategories() {
        _uiState.value = _uiState.value.copy(isLoadingCategories = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val categories = ApiClient.apiService.getCategories()
                val categoryItems = categories.map { dto ->
                    CategoryItem(
                        id = dto.id,
                        name = dto.name,
                        iconUrl = dto.icon_url,
                        colorHex = getCategoryColor(dto.id)
                    )
                }
                _uiState.value = _uiState.value.copy(
                    categories = categoryItems,
                    isLoadingCategories = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingCategories = false,
                    errorMessage = "Failed to load categories: ${e.message}"
                )
            }
        }
    }

    private fun loadNearbyHelpers(categoryId: Int? = null) {
        _uiState.value = _uiState.value.copy(isLoadingHelpers = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val helpers = ApiClient.apiService.getNearbyHelpers(
                    lat = _uiState.value.userLat,
                    lng = _uiState.value.userLng,
                    categoryId = categoryId,
                    maxDistanceKm = 25.0
                )
                _uiState.value = _uiState.value.copy(
                    nearbyHelpers = helpers,
                    isLoadingHelpers = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingHelpers = false,
                    errorMessage = "Failed to load helpers: ${e.message}"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterHelpers()
    }

    fun onCategorySelected(categoryId: Int?) {
        val newCategory = if (_uiState.value.selectedCategory == categoryId) null else categoryId
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
        loadNearbyHelpers(newCategory)
    }

    fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(isMapView = !_uiState.value.isMapView)
    }

    fun showLocationDialog() {
        _uiState.value = _uiState.value.copy(showLocationDialog = true)
    }

    fun hideLocationDialog() {
        _uiState.value = _uiState.value.copy(showLocationDialog = false)
    }

    fun selectPresetLocation(locationName: String, lat: Double, lng: Double) {
        updateUserLocation(lat, lng, locationName)
        hideLocationDialog()
    }

    fun updateUserLocation(lat: Double, lng: Double, locationName: String) {
        _uiState.value = _uiState.value.copy(
            userLat = lat,
            userLng = lng,
            userLocation = locationName
        )
        loadNearbyHelpers(_uiState.value.selectedCategory)
    }

    private fun filterHelpers() {
        val query = _uiState.value.searchQuery.lowercase()
        val categoryId = _uiState.value.selectedCategory

        if (query.isEmpty() && categoryId == null) {
            // No filters, reload from API
            loadNearbyHelpers(categoryId)
            return
        }

        // Client-side filtering for search query
        val filtered = _uiState.value.nearbyHelpers.filter { helper ->
            val matchesQuery = query.isEmpty() ||
                    helper.name.lowercase().contains(query) ||
                    helper.bio.lowercase().contains(query) ||
                    helper.category?.name?.lowercase()?.contains(query) == true

            val matchesCategory = categoryId == null || helper.category?.id == categoryId

            matchesQuery && matchesCategory
        }

        _uiState.value = _uiState.value.copy(nearbyHelpers = filtered)
    }

    private fun getCategoryColor(categoryId: Int): Long {
        // Assign consistent colors to categories
        return when (categoryId) {
            1 -> 0xFF7C3AED  // Personal Assistance - Purple
            2 -> 0xFF2563EB  // Electrical - Blue
            3 -> 0xFF10B981  // Tutoring - Green
            4 -> 0xFFF59E0B  // Photography - Amber
            5 -> 0xFFEF4444  // Home Repairs - Red
            6 -> 0xFF8B5CF6  // Errands - Violet
            7 -> 0xFFEC4899  // Design - Pink
            8 -> 0xFF06B6D4  // Business - Cyan
            else -> 0xFF6B7280 // Gray
        }
    }
}
