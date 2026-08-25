package com.marketplace.onehour.helper.presentation.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HelperProfileCreateViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HelperProfileCreateState())
    val uiState: StateFlow<HelperProfileCreateState> = _uiState.asStateFlow()

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun toggleSkill(skill: String) {
        val current = _uiState.value.selectedSkills
        val updated = if (current.contains(skill)) current - skill else current + skill
        _uiState.value = _uiState.value.copy(selectedSkills = updated)
    }

    fun onExperienceChanged(exp: String) {
        _uiState.value = _uiState.value.copy(experienceYears = exp)
    }

    fun onBioChanged(bio: String) {
        _uiState.value = _uiState.value.copy(bioText = bio)
    }
}
