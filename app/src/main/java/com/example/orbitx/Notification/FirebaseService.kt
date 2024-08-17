package com.example.orbitx

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Telephony.Sms.Conversations
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import coil.compose.rememberAsyncImagePainter

import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.Notification.AppState
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title2"]
            val body = remoteMessage.data["body2"]
            val userUid = remoteMessage.data["userUid"]
            val route = remoteMessage.data["route"]
            val receiverUid = remoteMessage.data["receiverUid"]
            if (!isChatWithCurrentUser(receiverUid)) {
                showNotificationWithUserUid(userUid, route, title, body, receiverUid)
            }
        }


    }

    private fun isChatWithCurrentUser(receiverUid: String?): Boolean {

        return receiverUid?.trim() == AppState.currentChatUid?.trim()
    }

    override fun onNewToken(token: String) {

        Log.d("FCM", "Refreshed token: $token")
    }


    private fun showNotificationWithUserUid(userUid: String?, route: String?, title: String?, body: String?, receiverUid: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random().nextInt()
        val channelId = "orbitx_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH

            )
            channel.description = "OrbitX Notifications"
            val soundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            channel.setSound(soundUri, null)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title ?: "Notification")
                    .setContentText(body ?: "You have a new message")
                    .setSmallIcon(R.drawable.chat__1)
                    .setColor(Color.GREEN)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setDefaults(NotificationCompat.PRIORITY_HIGH)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setContentIntent(createNotificationPendingIntent(route=route, userUid=userUid))

                notificationManager.notify(notificationId, notificationBuilder.build())
            }


    private fun createNotificationPendingIntent(route: String? = null, userUid: String? = null): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            route?.let {
                putExtra("EXTRA_SCREEN_ROUTE", it)
            }
            putExtra("uid", userUid)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
