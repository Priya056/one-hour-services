package com.marketplace.onehour.integration.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Custom FirebaseMessagingService for handling incoming FCM push payloads
 * and device token refreshes.
 */
class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "Refreshed FCM Device Token: $token")
        // TODO: Send new token to backend PUT /api/users/device-token endpoint
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCMService", "FCM Message Received from: ${remoteMessage.from}")

        // Check if message contains a notification payload
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "1-Hour Service Update"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "You have a new update regarding your booking."

        // Display local push notification
        val fcmHelper = FCMHelper(applicationContext)
        fcmHelper.showNotification(title, body)
    }
}
