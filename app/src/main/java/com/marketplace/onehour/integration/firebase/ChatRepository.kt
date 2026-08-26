package com.marketplace.onehour.integration.firebase

import android.util.Log
import com.marketplace.onehour.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * Data class representing a real-time chat message document.
 */
data class ChatMessage(
    val id: String = "",
    val bookingId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)

/**
 * Modular Firestore Chat Repository.
 * Evaluates BuildConfig.CHAT_MODE ("mock" vs "live_test").
 * In "mock" mode: serves seeded chat threads for b101-b105 from in-memory state.
 * In "live_test" mode: streams real-time messages from Firestore `chats/{bookingId}/messages`.
 */
class ChatRepository {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val inMemoryChatCache = mutableMapOf<String, MutableStateFlow<List<ChatMessage>>>()

    /**
     * Retrieves a real-time Flow stream of chat messages for a specific booking ID.
     */
    fun getMessagesStream(bookingId: String): Flow<List<ChatMessage>> = callbackFlow {
        val isMockMode = BuildConfig.CHAT_MODE.equals("mock", ignoreCase = true)

        if (isMockMode) {
            Log.d("ChatRepository", "CHAT_MODE=mock active. Streaming in-memory mock chat thread for booking #$bookingId")
            val cacheFlow = getCacheStateFlow(bookingId)
            val collector = launch {
                cacheFlow.collect { messages ->
                    trySend(messages)
                }
            }
            awaitClose { collector.cancel() }
            return@callbackFlow
        }

        // Live Test Mode with Firestore
        try {
            Log.d("ChatRepository", "CHAT_MODE=live_test active. Connecting to Firestore snapshot listener for chats/$bookingId/messages")
            val listenerRegistration = firestore.collection("chats")
                .document(bookingId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ChatRepository", "Firestore snapshot listener error for booking #$bookingId", error)
                        trySend(getInMemoryMessages(bookingId))
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val messages = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                        }
                        trySend(messages)
                    }
                }

            awaitClose { listenerRegistration.remove() }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Firestore connection exception, falling back to mock stream", e)
            trySend(getInMemoryMessages(bookingId))
            awaitClose { }
        }
    }

    /**
     * Sends a text message to Firestore or in-memory mock repository.
     */
    fun sendMessage(
        bookingId: String,
        senderId: String,
        senderName: String,
        text: String,
        onComplete: (Boolean) -> Unit
    ) {
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            bookingId = bookingId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            timestamp = System.currentTimeMillis(),
            isRead = true
        )

        // Always update in-memory state for immediate UI rendering
        appendToInMemoryCache(bookingId, newMsg)

        val isMockMode = BuildConfig.CHAT_MODE.equals("mock", ignoreCase = true)
        if (isMockMode) {
            Log.d("ChatRepository", "CHAT_MODE=mock: Appended message to mock stream for booking #$bookingId")
            onComplete(true)
            return
        }

        try {
            firestore.collection("chats")
                .document(bookingId)
                .collection("messages")
                .add(newMsg)
                .addOnSuccessListener {
                    Log.d("ChatRepository", "Message sent successfully to Firestore: ${it.id}")
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.e("ChatRepository", "Failed to push message to Firestore", e)
                    onComplete(true)
                }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Firestore instance exception during send", e)
            onComplete(true)
        }
    }

    /**
     * Helper to get unread count for badge/UI testing.
     */
    fun getUnreadCount(bookingId: String): Int {
        return getInMemoryMessages(bookingId).count { !it.isRead }
    }

    private fun getCacheStateFlow(bookingId: String): MutableStateFlow<List<ChatMessage>> {
        return inMemoryChatCache.getOrPut(bookingId) {
            MutableStateFlow(createInitialMockMessages(bookingId))
        }
    }

    private fun getInMemoryMessages(bookingId: String): List<ChatMessage> {
        return getCacheStateFlow(bookingId).value
    }

    private fun appendToInMemoryCache(bookingId: String, message: ChatMessage) {
        val cacheFlow = getCacheStateFlow(bookingId)
        val currentList = cacheFlow.value.toMutableList()
        currentList.add(message)
        cacheFlow.value = currentList
    }

    private fun createInitialMockMessages(bookingId: String): List<ChatMessage> {
        val now = System.currentTimeMillis()
        return when (bookingId) {
            "b101" -> listOf(
                ChatMessage("m101_1", "b101", "h1", "Alex Rivera", "Hi Priya! I have accepted your 1-hour booking for electrical repair.", now - 600000),
                ChatMessage("m101_2", "b101", "u101", "Priya Sharma", "Hi Alex, are you on your way?", now - 400000),
                ChatMessage("m101_3", "b101", "h1", "Alex Rivera", "Yes, I am near 4th Cross! Will reach in 5 mins.", now - 200000),
                ChatMessage("m101_4", "b101", "u101", "Priya Sharma", "Great, I have the circuit breaker panel open for you.", now - 60000),
                ChatMessage("m101_5", "b101", "h1", "Alex Rivera", "Perfect, starting diagnostic work now.", now - 30000)
            )
            "b102" -> listOf(
                ChatMessage("m102_1", "b102", "h2", "Sarah Jenkins", "Hello Ananya, I've picked up your grocery order from the supermarket.", now - 500000),
                ChatMessage("m102_2", "b102", "u102", "Ananya Verma", "Awesome! Please don't forget the receipt.", now - 300000),
                ChatMessage("m102_3", "b102", "h2", "Sarah Jenkins", "Receipt is attached in the bag. On my way now!", now - 100000)
            )
            "b103" -> listOf(
                ChatMessage("m103_1", "b103", "h3", "Marcus Vance", "Hi Priya, looking forward to our portrait photo session today at 5 PM.", now - 800000),
                ChatMessage("m103_2", "b103", "u101", "Priya Sharma", "Will we be doing studio lighting or outdoor natural light?", now - 600000),
                ChatMessage("m103_3", "b103", "h3", "Marcus Vance", "We'll start with natural light at Gachibowli Park!", now - 400000),
                ChatMessage("m103_4", "b103", "h3", "Marcus Vance", "Let me know if you need to reschedule or change location.", now - 60000, isRead = false) // Unread message for notification badge testing
            )
            "b104" -> listOf(
                ChatMessage("m104_1", "b104", "h1", "Alex Rivera", "Hi Rahul, booking was cancelled due to scheduling conflict.", now - 900000),
                ChatMessage("m104_2", "b104", "u103", "Rahul Mehta", "No problem, I will rebook for tomorrow.", now - 700000)
            )
            "b105" -> listOf(
                ChatMessage("m105_1", "b105", "h4", "David Chen", "Hi Ananya, Calculus study materials are sent to your email.", now - 1000000),
                ChatMessage("m105_2", "b105", "u102", "Ananya Verma", "Thank you David! See you for the 1-hour session.", now - 800000)
            )
            else -> listOf(
                ChatMessage("m_def_1", bookingId, "h1", "Alex Rivera", "Hello! Welcome to Lumina 1-Hour Local Services.", now - 300000),
                ChatMessage("m_def_2", bookingId, "u101", "Priya Sharma", "Hi! Thanks for accepting the booking.", now - 100000)
            )
        }
    }
}
