package com.marketplace.onehour.customer.presentation.chat

import com.marketplace.onehour.common.network.HelperDto
import com.marketplace.onehour.integration.firebase.ChatMessage

data class ChatState(
    val bookingId: String = "b101",
    val helper: HelperDto? = null,
    val messageText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isOnline: Boolean = true,
    val isLoading: Boolean = false
)
