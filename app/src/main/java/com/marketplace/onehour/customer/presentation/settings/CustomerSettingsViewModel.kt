package com.marketplace.onehour.customer.presentation.settings

import androidx.lifecycle.ViewModel
import com.marketplace.onehour.common.network.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomerSettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        CustomerSettingsState(
            name = TokenStore.getName().orEmpty(),
            phone = TokenStore.getPhone().orEmpty(),
            email = TokenStore.getEmail().orEmpty()
        )
    )
    val uiState: StateFlow<CustomerSettingsState> = _uiState.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }

    fun setDefaultAddress(addressId: String) {
        val updated = _uiState.value.addresses.map {
            it.copy(isDefault = (it.id == addressId))
        }
        _uiState.value = _uiState.value.copy(addresses = updated)
    }
}
