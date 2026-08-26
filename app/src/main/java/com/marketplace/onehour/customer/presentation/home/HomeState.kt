package com.marketplace.onehour.customer.presentation.home

import com.marketplace.onehour.common.network.CategoryDto
import com.marketplace.onehour.common.network.HelperDto

data class CategoryItem(
    val id: Int,
    val name: String,
    val iconUrl: String?,
    val colorHex: Long
)

data class HomeState(
    val searchQuery: String = "",
    val selectedCategory: Int? = null,
    val isMapView: Boolean = false,
    val userLocation: String = "Indiranagar, Bengaluru",
    val userLat: Double = 12.9716,
    val userLng: Double = 77.5946,
    val categories: List<CategoryItem> = emptyList(),
    val nearbyHelpers: List<HelperDto> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingCategories: Boolean = false,
    val isLoadingHelpers: Boolean = false,
    val errorMessage: String? = null,
    val unreadNotificationsCount: Int = 2,
    val showLocationDialog: Boolean = false
)
