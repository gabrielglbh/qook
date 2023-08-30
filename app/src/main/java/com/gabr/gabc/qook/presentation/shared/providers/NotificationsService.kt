package com.gabr.gabc.qook.presentation.shared.providers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.splashPage.SplashPage
import com.gabr.gabc.qook.presentation.theme.seed
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Date

class NotificationsService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {}

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            remoteMessage.notification?.let { notification ->
                val notifyIntent = Intent(this, SplashPage::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val notifyPendingIntent = PendingIntent.getActivity(
                    this, 0, notifyIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val channelId = getString(R.string.default_notification_channel_id)
                val mNotificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                val channel = NotificationChannel(
                    channelId,
                    "Main channel for QOOK updates",
                    NotificationManager.IMPORTANCE_HIGH
                )
                mNotificationManager.createNotificationChannel(channel)

                val builder = NotificationCompat.Builder(this, channelId).apply {
                    setContentIntent(notifyPendingIntent)
                    setSmallIcon(R.mipmap.ic_launcher)
                    color = seed.toArgb()
                    setChannelId(channelId)
                    setAutoCancel(true)
                    setStyle(NotificationCompat.BigTextStyle().bigText(notification.body))
                    setVibrate(longArrayOf(2000, 2000))
                    setContentTitle(notification.title)
                    setContentText(notification.body)
                }

                with(NotificationManagerCompat.from(this)) {
                    if (ActivityCompat.checkSelfPermission(
                            this@NotificationsService,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        notify(((Date().time / 1000L % Int.MAX_VALUE).toInt()), builder.build())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDeletedMessages() {}

    /*private fun updateTokenToUser(token: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val user = UserQueries().getUser(SharedPreferences.userId, SharedPreferences.groupId)
            user?.let { u ->
                u.messagingToken = token
                UserQueries().updateUser(u, SharedPreferences.groupId)
            }
        }
    }*/
}