package com.marketplace.onehour.helper.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    // Becoming a helper is a one-time server-side action (it flips the
    // user's role and creates a HelperProfile row) — re-running onboarding
    // for someone who already has one would fail. Check first so a
    // returning helper skips straight to their dashboard.
    fun checkExistingHelperStatus() {
        viewModelScope.launch {
            try {
                ApiClient.api.getHelperProfile()
                _uiState.update { it.copy(alreadyHelper = true) }
            } catch (e: HttpException) {
                _uiState.update { it.copy(alreadyHelper = false) }
            } catch (e: Exception) {
                // Network hiccup — don't block the onboarding flow on it.
            }
        }
    }
}
