package com.marketplace.onehour.helper.presentation.wallet

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

data class WalletTransactionItem(
    val id: String,
    val bookingId: String?,
    val title: String,
    val date: String,
    val amount: String,
    val type: String, // "credit" or "withdrawal"
    val status: String
)

data class WalletState(
    val walletBalance: String = "₹0.00",
    val lifetimeEarnings: String = "₹0.00",
    // Matches PlatformSettingsSeeder's default_commission_percent — there's
    // no API exposing platform settings yet, so this mirrors the seeded
    // constant rather than being per-user data.
    val platformCommissionRate: String = "15%",
    val transactions: List<WalletTransactionItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class WalletViewModel : ViewModel() {
    private val _state = MutableStateFlow(WalletState())
    val state: StateFlow<WalletState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val wallet = ApiClient.api.getWallet().data
                val lifetimeEarnings = wallet.transactions
                    .filter { it.type == "credit" }
                    .sumOf { it.amount }
                val items = wallet.transactions
                    .sortedByDescending { it.createdAt }
                    .map {
                        WalletTransactionItem(
                            id = it.id.toString(),
                            bookingId = it.bookingId?.toString(),
                            title = if (it.type == "credit") {
                                "Booking #${it.bookingId} Payout"
                            } else {
                                "Withdrawal Requested"
                            },
                            date = formatDate(it.createdAt),
                            amount = "${if (it.type == "credit") "+" else "-"}₹%.2f".format(it.amount),
                            type = it.type,
                            status = it.status
                        )
                    }
                _state.update {
                    it.copy(
                        walletBalance = "₹%.2f".format(wallet.balance),
                        lifetimeEarnings = "₹%.2f".format(lifetimeEarnings),
                        transactions = items,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Couldn't load wallet: ${e.message}") }
            }
        }
    }

    private fun formatDate(isoTime: String): String = try {
        OffsetDateTime.parse(isoTime).format(DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a"))
    } catch (e: Exception) {
        isoTime
    }
}
