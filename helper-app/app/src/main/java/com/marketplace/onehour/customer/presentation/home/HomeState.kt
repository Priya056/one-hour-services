package com.marketplace.onehour.customer.presentation.home

import com.marketplace.onehour.common.network.HelperDto

data class CategoryItem(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: Long
)

data class HomeState(
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isMapView: Boolean = false,
    val userLocation: String = "Indiranagar, Bengaluru",
    val categories: List<CategoryItem> = emptyList(),
    val nearbyHelpers: List<HelperDto> = emptyList(),
    val isLoading: Boolean = false,
    val unreadNotificationsCount: Int = 2,
    val errorMessage: String? = null
)
