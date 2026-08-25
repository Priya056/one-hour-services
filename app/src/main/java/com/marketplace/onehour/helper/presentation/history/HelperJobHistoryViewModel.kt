package com.marketplace.onehour.helper.presentation.history

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

data class HistoryJobItem(
    val bookingId: String,
    val customerName: String,
    val category: String,
    val date: String,
    val amount: String,
    val status: String // COMPLETED, CANCELLED
)

data class HelperJobHistoryState(
    val jobs: List<HistoryJobItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HelperJobHistoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(HelperJobHistoryState())
    val state: StateFlow<HelperJobHistoryState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val jobs = ApiClient.api.getBookingsRaw().data
                    .filter { it.status == "completed" || it.status == "cancelled" }
                    .sortedByDescending { it.scheduledTime }
                    .map {
                        HistoryJobItem(
                            bookingId = "BK-${it.id}",
                            customerName = it.customer?.name.orEmpty(),
                            category = it.category?.name.orEmpty(),
                            date = formatDate(it.scheduledTime),
                            amount = "₹%.2f".format(it.totalAmount),
                            status = it.status.uppercase()
                        )
                    }
                _state.update { it.copy(jobs = jobs, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Couldn't load job history: ${e.message}") }
            }
        }
    }

    private fun formatDate(isoTime: String): String = try {
        OffsetDateTime.parse(isoTime).format(DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a"))
    } catch (e: Exception) {
        isoTime
    }
}
