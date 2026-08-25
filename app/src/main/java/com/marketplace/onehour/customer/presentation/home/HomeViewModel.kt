package com.marketplace.onehour.customer.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.MockDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val categories = listOf(
            CategoryItem("1", "Personal Assistance", "Person", 0xFF7C3AED),
            CategoryItem("2", "Electrical", "Bolt", 0xFF2563EB),
            CategoryItem("3", "Tutoring", "School", 0xFF10B981),
            CategoryItem("4", "Photography", "Camera", 0xFFF59E0B),
            CategoryItem("5", "Home Repairs", "Build", 0xFFEF4444),
            CategoryItem("6", "Errands", "DirectionsRun", 0xFF8B5CF6),
            CategoryItem("7", "Design", "Palette", 0xFFEC4899),
            CategoryItem("8", "Business Assistance", "BusinessCenter", 0xFF06B6D4)
        )

        _uiState.value = _uiState.value.copy(
            categories = categories,
            nearbyHelpers = MockDataProvider.sampleHelpers
        )
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        filterHelpers()
    }

    fun onCategorySelected(categoryId: String?) {
        val newCategory = if (_uiState.value.selectedCategory == categoryId) null else categoryId
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
        filterHelpers()
    }

    fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(isMapView = !_uiState.value.isMapView)
    }

    private fun filterHelpers() {
        val query = _uiState.value.searchQuery.lowercase()
        val categoryId = _uiState.value.selectedCategory

        val filtered = MockDataProvider.sampleHelpers.filter { helper ->
            val matchesQuery = query.isEmpty() ||
                    helper.name.lowercase().contains(query) ||
                    helper.mainCategory.lowercase().contains(query) ||
                    helper.skills.any { it.lowercase().contains(query) }

            val selectedCategoryName = _uiState.value.categories.find { it.id == categoryId }?.name
            val matchesCategory = selectedCategoryName == null || helper.mainCategory.equals(selectedCategoryName, ignoreCase = true)

            matchesQuery && matchesCategory
        }

        _uiState.value = _uiState.value.copy(nearbyHelpers = filtered)
    }
}
