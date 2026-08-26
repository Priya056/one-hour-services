package com.marketplace.onehour.common.placeholders

import android.util.Log
import com.marketplace.onehour.integration.firebase.ChatMessage
import com.marketplace.onehour.integration.firebase.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Firebase Firestore Chat Service integration point.
 * Delegates real-time message stream listening and message sending to ChatRepository.
 */
object FirebaseChatPlaceholder {

    private val repository = ChatRepository()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun listenToChatMessages(
        bookingId: String,
        onNewMessage: (List<ChatMessage>) -> Unit
    ) {
        Log.d("FirebaseChat", "Subscribing to Firestore real-time collection: chats/$bookingId/messages")
        scope.launch {
            repository.getMessagesStream(bookingId).collectLatest { messages ->
                onNewMessage(messages)
            }
        }
    }

    fun sendChatMessage(
        bookingId: String,
        message: ChatMessage,
        onComplete: (Boolean) -> Unit
    ) {
        Log.d("FirebaseChat", "Dispatching message to Firestore collection chats/$bookingId: ${message.text}")
        repository.sendMessage(
            bookingId = bookingId,
            senderId = message.senderId,
            senderName = message.senderName,
            text = message.text,
            onComplete = onComplete
        )
    }
}
