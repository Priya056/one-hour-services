package com.marketplace.onehour.customer.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.MockDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HelperProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HelperProfileState())
    val uiState: StateFlow<HelperProfileState> = _uiState.asStateFlow()

    fun loadHelperProfile(helperId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(400)
            val foundHelper = MockDataProvider.sampleHelpers.find { it.id == helperId }
                ?: MockDataProvider.sampleHelpers.first()

            val mockReviews = listOf(
                ReviewItem("r1", "Michael Brown", 5, "Arrived in 15 mins! Fixed our living room circuit breaker immediately. Excellent 1-hour service.", "2 days ago"),
                ReviewItem("r2", "Priya Sharma", 5, "Super professional and polite. Finished the job within the 1 hour window. Highly recommended!", "1 week ago"),
                ReviewItem("r3", "David Kim", 4, "Great experience, very knowledgeable about fittings and fast troubleshooting.", "2 weeks ago")
            )

            _uiState.value = HelperProfileState(
                helper = foundHelper,
                reviews = mockReviews,
                isLoading = false
            )
        }
    }
}
