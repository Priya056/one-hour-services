package com.marketplace.onehour.customer.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.HelperRepository
import com.marketplace.onehour.common.placeholders.FirebaseChatPlaceholder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    fun loadChatDetails(bookingId: String, helperId: String) {
        viewModelScope.launch {
            val helper = HelperRepository.findById(helperId) ?: HelperRepository.all().firstOrNull()

            val initialMessages = listOf(
                FirebaseChatPlaceholder.ChatMessage("m1", "h1", "c1", "Hi! I have accepted your 1-hour booking for electrical repair.", System.currentTimeMillis() - 600000),
                FirebaseChatPlaceholder.ChatMessage("m2", "c1", "h1", "Hi Alex, are you on your way?", System.currentTimeMillis() - 400000),
                FirebaseChatPlaceholder.ChatMessage("m3", "h1", "c1", "Yes, I am near 4th Cross! Will reach in 5 mins.", System.currentTimeMillis() - 200000),
                FirebaseChatPlaceholder.ChatMessage("m4", "c1", "h1", "Great, I have the circuit breaker panel open for you.", System.currentTimeMillis() - 60000)
            )

            _uiState.value = _uiState.value.copy(
                bookingId = bookingId,
                helper = helper,
                messages = initialMessages
            )
        }
    }

    fun onMessageTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(messageText = text)
    }

    fun sendMessage(textOverride: String? = null) {
        val text = textOverride ?: _uiState.value.messageText
        if (text.isBlank()) return

        val newMessage = FirebaseChatPlaceholder.ChatMessage(
            id = "m_${System.currentTimeMillis()}",
            senderId = "c1",
            receiverId = _uiState.value.helper?.id ?: "h1",
            text = text,
            timestamp = System.currentTimeMillis()
        )

        // TODO: In production, push message to Firebase Firestore sub-collection
        FirebaseChatPlaceholder.sendChatMessage(_uiState.value.bookingId, newMessage) {
            val updatedList = _uiState.value.messages + newMessage
            _uiState.value = _uiState.value.copy(
                messages = updatedList,
                messageText = ""
            )
        }

        // Simulate automatic helper reply after 2 seconds
        viewModelScope.launch {
            delay(2000)
            val helperReply = FirebaseChatPlaceholder.ChatMessage(
                id = "m_reply_${System.currentTimeMillis()}",
                senderId = _uiState.value.helper?.id ?: "h1",
                receiverId = "c1",
                text = "Got it! Thanks for letting me know.",
                timestamp = System.currentTimeMillis()
            )
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + helperReply
            )
        }
    }
}
