package com.marketplace.onehour.customer.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.HelperRepository
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

            val foundHelper = HelperRepository.findById(helperId)
            val helperIdInt = helperId.toIntOrNull()
            val reviews = if (helperIdInt != null) {
                try {
                    ApiClient.api.getHelperReviews(helperIdInt).data.map { dto ->
                        ReviewItem(
                            id = dto.id.toString(),
                            reviewerName = "Customer #${dto.customerId}",
                            rating = dto.rating,
                            comment = dto.comment ?: "",
                            date = ""
                        )
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }

            _uiState.value = HelperProfileState(
                helper = foundHelper,
                reviews = reviews,
                isLoading = false
            )
        }
    }
}
