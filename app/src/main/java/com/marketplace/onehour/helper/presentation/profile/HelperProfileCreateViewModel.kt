package com.marketplace.onehour.helper.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.helper.presentation.onboarding.HelperOnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HelperProfileCreateViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HelperProfileCreateState())
    val uiState: StateFlow<HelperProfileCreateState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val categories = ApiClient.api.getCategories().data
                val first = categories.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    categories = categories,
                    selectedCategoryId = first?.id,
                    selectedCategoryName = first?.name ?: "",
                    isLoading = false
                )
                HelperOnboardingRepository.categoryId = first?.id
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun onCategorySelected(categoryId: Int, categoryName: String) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId, selectedCategoryName = categoryName)
        HelperOnboardingRepository.categoryId = categoryId
    }

    fun toggleSkill(skill: String) {
        val current = _uiState.value.selectedSkills
        val updated = if (current.contains(skill)) current - skill else current + skill
        _uiState.value = _uiState.value.copy(selectedSkills = updated)
    }

    fun onExperienceChanged(exp: String) {
        _uiState.value = _uiState.value.copy(experienceYears = exp)
        HelperOnboardingRepository.experienceYears = exp.filter { it.isDigit() }.toIntOrNull() ?: 0
    }

    fun onBioChanged(bio: String) {
        _uiState.value = _uiState.value.copy(bioText = bio)
        HelperOnboardingRepository.bio = bio
    }
}
