package com.marketplace.onehour.integration.firebase

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.marketplace.onehour.BuildConfig
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Modular Firebase Cloud Messaging (FCM) Helper.
 * Evaluates BuildConfig.NOTIFICATION_MODE ("mock" vs "live_test").
 * Handles device token retrieval, Android 13+ POST_NOTIFICATIONS runtime permission requests,
 * and displaying local/simulated test notifications.
 */
class FCMHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "lumina_booking_notifications"
        const val CHANNEL_NAME = "1-Hour Service Updates"
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
    }

    init {
        createNotificationChannel()
    }

    /**
     * Check if POST_NOTIFICATIONS runtime permission is granted (Required on Android 13+ / API 33+).
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission automatically granted on Android 12 and below
        }
    }

    /**
     * Request POST_NOTIFICATIONS runtime permission on Android 13+.
     */
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission()) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    /**
     * Retrieve the FCM device registration token.
     */
    fun getDeviceToken(
        onSuccess: (token: String) -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {
        val isMockMode = BuildConfig.NOTIFICATION_MODE.equals("mock", ignoreCase = true)
        if (isMockMode) {
            Log.d("FCMHelper", "NOTIFICATION_MODE=mock active. Returning mock FCM token.")
            onSuccess("fcm_mock_token_${System.currentTimeMillis()}")
            return
        }

        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d("FCMHelper", "FCM Device Registration Token: $token")
                    onSuccess(token)
                }
                .addOnFailureListener { exception ->
                    Log.e("FCMHelper", "Failed to retrieve FCM token, using fallback mock token", exception)
                    val mockToken = "fcm_mock_token_${System.currentTimeMillis()}"
                    onSuccess(mockToken)
                }
        } catch (e: Exception) {
            Log.e("FCMHelper", "FirebaseMessaging instance error", e)
            val mockToken = "fcm_mock_token_${System.currentTimeMillis()}"
            onSuccess(mockToken)
        }
    }

    /**
     * Simulates local booking status notifications (Accepted, On the way, Completed).
     *
     * TODO: Trigger booking-status push notifications (accepted/on the way/completed)
     * from backend FCM Admin SDK when real server is connected.
     */
    fun simulateBookingNotification(bookingId: String, statusTitle: String, helperName: String = "Alex Rivera") {
        val (title, body) = when (statusTitle.lowercase()) {
            "accepted" -> Pair(
                "Booking #$bookingId Accepted!",
                "Helper $helperName has accepted your 1-hour service request."
            )
            "on the way", "ontheway" -> Pair(
                "Helper On The Way! 🛵",
                "Helper $helperName is heading to your location now."
            )
            "completed" -> Pair(
                "Service Completed! ⭐",
                "Your 1-hour service with $helperName is complete. Tap to rate & review."
            )
            else -> Pair(
                "Booking Update: #$bookingId",
                "Status changed to $statusTitle"
            )
        }

        showNotification(title, body, notificationId = (bookingId.hashCode() and 0x7FFFFFFF))
    }

    /**
     * Display a local push notification.
     */
    fun showNotification(title: String, message: String, notificationId: Int = 101) {
        if (!hasNotificationPermission()) {
            Log.w("FCMHelper", "Cannot show notification: POST_NOTIFICATIONS permission not granted.")
            return
        }

        try {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notificationManager.notify(notificationId, builder.build())
            }
        } catch (e: Exception) {
            Log.e("FCMHelper", "Error displaying notification", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for 1-hour service booking status changes and chat messages"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
