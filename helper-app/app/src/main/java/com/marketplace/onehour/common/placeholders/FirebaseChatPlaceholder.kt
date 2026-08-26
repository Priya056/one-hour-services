package com.marketplace.onehour.common.placeholders

/**
 * Firebase Firestore Chat Service integration placeholder.
 * TODO: Replace mock list listener with FirebaseFirestore.getInstance().collection("chats").
 */
object FirebaseChatPlaceholder {
    data class ChatMessage(
        val id: String,
        val senderId: String,
        val receiverId: String,
        val text: String,
        val timestamp: Long
    )

    fun sendChatMessage(bookingId: String, message: ChatMessage, onComplete: (Boolean) -> Unit) {
        // TODO: Push message document to Firestore sub-collection
        onComplete(true)
    }
}
