package com.marketplace.onehour.helper.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class CustomerReviewItem(
    val id: String,
    val customerName: String,
    val rating: Int,
    val comment: String,
    val date: String
)

data class HelperReviewsState(
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val reviews: List<CustomerReviewItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HelperReviewsViewModel : ViewModel() {
    private val _state = MutableStateFlow(HelperReviewsState())
    val state: StateFlow<HelperReviewsState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val profile = ApiClient.api.getHelperProfile().data
                val reviews = ApiClient.api.getHelperReviews(profile.id).data.map {
                    CustomerReviewItem(
                        id = it.id.toString(),
                        customerName = it.customer?.name.orEmpty(),
                        rating = it.rating,
                        comment = it.comment.orEmpty(),
                        date = it.createdAt?.let(::formatDate) ?: ""
                    )
                }
                _state.update {
                    it.copy(
                        averageRating = profile.averageRating,
                        totalReviews = profile.totalReviews,
                        reviews = reviews,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Couldn't load reviews: ${e.message}") }
            }
        }
    }

    private fun formatDate(isoTime: String): String = try {
        OffsetDateTime.parse(isoTime).format(DateTimeFormatter.ofPattern("d MMM yyyy"))
    } catch (e: Exception) {
        isoTime
    }
}
