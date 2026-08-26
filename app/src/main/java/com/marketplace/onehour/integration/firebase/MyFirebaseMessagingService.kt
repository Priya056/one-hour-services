package com.marketplace.onehour.integration.firebase

import android.util.Log
import com.marketplace.onehour.common.placeholders.FCMPlaceholder

/**
 * Firebase Cloud Messaging Service.
 * Handles FCM registration tokens and incoming push messages.
 */
class MyFirebaseMessagingService {
    fun onNewToken(token: String) {
        Log.d("FCMService", "Refreshed FCM device registration token: $token")
        FCMPlaceholder.registerDeviceToken("u101") { /* Token updated */ }
    }

    fun onMessageReceived(title: String, body: String) {
        Log.d("FCMService", "Incoming FCM Push Notification: $title - $body")
        FCMPlaceholder.handleIncomingNotification(title, body)
    }
}
