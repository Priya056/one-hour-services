package com.marketplace.onehour.common.placeholders

/**
 * Firebase Cloud Messaging (FCM) Integration placeholder.
 * TODO: Add google-services.json and attach FirebaseMessaging.getInstance().token.
 */
object FCMPlaceholder {
    fun registerDeviceToken(userId: String, onTokenReceived: (String) -> Unit) {
        val mockToken = "fcm_token_mock_${userId}_${System.currentTimeMillis()}"
        onTokenReceived(mockToken)
    }

    fun handleIncomingNotification(title: String, message: String) {
        // TODO: Handle push notifications in foreground/background
    }
}
