package com.marketplace.onehour.helper.presentation.activejob

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.UpdateBookingStatusRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class JobLifecycleStatus {
    ACCEPTED,
    ON_THE_WAY,
    ARRIVED,
    IN_PROGRESS,
    COMPLETED
}

private fun JobLifecycleStatus.toBackendStatus(): String = when (this) {
    JobLifecycleStatus.ACCEPTED -> "accepted"
    JobLifecycleStatus.ON_THE_WAY -> "on_the_way"
    JobLifecycleStatus.ARRIVED -> "on_the_way"
    JobLifecycleStatus.IN_PROGRESS -> "in_progress"
    JobLifecycleStatus.COMPLETED -> "completed"
}

private fun String.toLifecycleStatus(): JobLifecycleStatus = when (this) {
    "accepted" -> JobLifecycleStatus.ACCEPTED
    "on_the_way" -> JobLifecycleStatus.ON_THE_WAY
    "in_progress" -> JobLifecycleStatus.IN_PROGRESS
    "completed" -> JobLifecycleStatus.COMPLETED
    else -> JobLifecycleStatus.ACCEPTED
}

data class ActiveJobState(
    val bookingId: Int? = null,
    val customerName: String = "",
    val customerPhone: String = "",
    val address: String = "",
    val serviceCategory: String = "",
    val totalAmount: String = "",
    val status: JobLifecycleStatus = JobLifecycleStatus.ACCEPTED,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ActiveJobViewModel : ViewModel() {
    private val _state = MutableStateFlow(ActiveJobState())
    val state: StateFlow<ActiveJobState> = _state.asStateFlow()

    fun loadJob(bookingId: Int) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val booking = ApiClient.api.getBooking(bookingId).data
                _state.update {
                    it.copy(
                        bookingId = booking.id,
                        customerName = booking.customer?.name.orEmpty(),
                        customerPhone = booking.customer?.phone.orEmpty(),
                        address = booking.addressText,
                        serviceCategory = booking.category?.name.orEmpty(),
                        totalAmount = "₹%.2f".format(booking.totalAmount),
                        status = booking.status.toLifecycleStatus(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Couldn't load job: ${e.message}") }
            }
        }
    }

    fun advanceTo(newStatus: JobLifecycleStatus) {
        // ARRIVED has no backend counterpart — it's a local waypoint before
        // the real "in_progress" transition, so just update UI state for it.
        if (newStatus == JobLifecycleStatus.ARRIVED) {
            _state.update { it.copy(status = newStatus) }
            return
        }

        val bookingId = _state.value.bookingId ?: return
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val booking = ApiClient.api.updateBookingStatus(
                    bookingId,
                    UpdateBookingStatusRequestBody(status = newStatus.toBackendStatus())
                ).data
                _state.update { it.copy(status = booking.status.toLifecycleStatus(), isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Couldn't update job: ${e.message}") }
            }
        }
    }
}
