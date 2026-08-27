package com.marketplace.onehour.customer.presentation.settings

data class AddressItem(
    val id: String,
    val label: String,
    val fullAddress: String,
    val isDefault: Boolean
)

data class CustomerSettingsState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val addresses: List<AddressItem> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: String = "English",
    val isLoading: Boolean = false
)
