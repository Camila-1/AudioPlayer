package com.example.audioplayer.services.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.audioplayer.R

class PlayerNotification(private val context: Context) {

    fun buildNotification(): Notification? {
        val chan = NotificationChannel(
            "MyChannelId",
            "My Foreground Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val builder = NotificationCompat.Builder(context, "Music")
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_play_circle_filled_24)
            .setContentTitle("App is running on foreground")
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setChannelId("MyChannelId")

        return builder.build()
    }
}