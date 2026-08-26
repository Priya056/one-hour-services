package com.marketplace.onehour.helper.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.ApiClient
import com.marketplace.onehour.common.network.WithdrawRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WithdrawalState(
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null
)

class WithdrawalViewModel : ViewModel() {
    private val _state = MutableStateFlow(WithdrawalState())
    val state: StateFlow<WithdrawalState> = _state.asStateFlow()

    fun submitWithdrawal(amount: Double, payoutType: String, upiId: String, accountNumber: String, ifscCode: String) {
        val bankDetails = if (payoutType == "UPI") {
            mapOf("method" to "upi", "upi_id" to upiId)
        } else {
            mapOf("method" to "bank", "account_number" to accountNumber, "ifsc_code" to ifscCode)
        }
        _state.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                ApiClient.api.withdraw(WithdrawRequestBody(amount = amount, bankAccountDetails = bankDetails))
                _state.update { it.copy(isSubmitting = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, errorMessage = "Couldn't submit withdrawal: ${e.message}") }
            }
        }
    }
}
