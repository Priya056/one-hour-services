package com.marketplace.onehour.customer.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marketplace.onehour.common.network.MockDataProvider
import com.marketplace.onehour.common.placeholders.FirebaseChatPlaceholder
import com.marketplace.onehour.integration.firebase.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    fun loadChatDetails(bookingId: String, helperId: String) {
        viewModelScope.launch {
            val helper = MockDataProvider.sampleHelpers.find { it.id == helperId }
                ?: MockDataProvider.sampleHelpers.first()

            _uiState.value = _uiState.value.copy(
                bookingId = bookingId,
                helper = helper
            )

            // Subscribe to Firestore real-time message stream
            FirebaseChatPlaceholder.listenToChatMessages(bookingId) { incomingMessages ->
                _uiState.value = _uiState.value.copy(messages = incomingMessages)
            }
        }
    }

    fun onMessageTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(messageText = text)
    }

    fun sendMessage(textOverride: String? = null) {
        val text = textOverride ?: _uiState.value.messageText
        if (text.isBlank()) return

        val newMessage = ChatMessage(
            id = "m_${System.currentTimeMillis()}",
            bookingId = _uiState.value.bookingId,
            senderId = "u101",
            senderName = "Priya Sharma",
            text = text,
            timestamp = System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(messageText = "")

        // Dispatch to Firestore / ChatRepository stream
        FirebaseChatPlaceholder.sendChatMessage(_uiState.value.bookingId, newMessage) {
            // Real-time snapshot listener automatically updates UI list
        }
    }
}
