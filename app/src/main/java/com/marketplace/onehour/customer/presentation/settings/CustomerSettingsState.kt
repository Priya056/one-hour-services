package com.marketplace.onehour.customer.presentation.settings

data class AddressItem(
    val id: String,
    val label: String,
    val fullAddress: String,
    val isDefault: Boolean
)

data class CustomerSettingsState(
    val name: String = "Priya Sharma",
    val phone: String = "+91 98765 43210",
    val email: String = "priya.sharma@example.com",
    val avatarUrl: String = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
    val addresses: List<AddressItem> = listOf(
        AddressItem("a1", "Home", "124, 4th Cross Rd, Indiranagar, Bengaluru - 560038", true),
        AddressItem("a2", "Work", "Tech Park, Outer Ring Rd, Bellandur, Bengaluru - 560103", false)
    ),
    val notificationsEnabled: Boolean = true,
    val selectedLanguage: String = "English",
    val isLoading: Boolean = false
)
