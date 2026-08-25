package com.marketplace.onehour.customer.presentation.chat

import com.marketplace.onehour.common.network.HelperDto
import com.marketplace.onehour.common.placeholders.FirebaseChatPlaceholder

data class ChatState(
    val bookingId: String = "b101",
    val helper: HelperDto? = null,
    val messageText: String = "",
    val messages: List<FirebaseChatPlaceholder.ChatMessage> = emptyList(),
    val isOnline: Boolean = true,
    val isLoading: Boolean = false
)
