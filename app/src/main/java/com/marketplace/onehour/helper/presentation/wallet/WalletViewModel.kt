package com.marketplace.onehour.helper.presentation.wallet

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WalletTransactionItem(
    val id: String,
    val bookingId: String?,
    val title: String,
    val date: String,
    val amount: String,
    val type: String, // "credit" or "withdrawal"
    val status: String // "completed" or "pending"
)

data class WalletState(
    val walletBalance: String = "₹4,250.00",
    val lifetimeEarnings: String = "₹28,500.00",
    val platformCommissionRate: String = "15%",
    val pendingWithdrawal: String = "₹0.00",
    val transactions: List<WalletTransactionItem> = listOf(
        WalletTransactionItem("TXN-101", "BK-8842", "Electrical Repair Payout", "Today, 3:45 PM", "+₹424.15", "credit", "completed"),
        WalletTransactionItem("TXN-100", "BK-8835", "Switchboard Install Payout", "Yesterday", "+₹509.15", "credit", "completed"),
        WalletTransactionItem("TXN-099", null, "UPI Bank Withdrawal (HDFC)", "23 Aug 2026", "-₹2,000.00", "withdrawal", "completed"),
        WalletTransactionItem("TXN-098", "BK-8812", "Home Repair Payout", "22 Aug 2026", "+₹679.15", "credit", "completed")
    )
)

class WalletViewModel : ViewModel() {
    private val _state = MutableStateFlow(WalletState())
    val state: StateFlow<WalletState> = _state.asStateFlow()
}
