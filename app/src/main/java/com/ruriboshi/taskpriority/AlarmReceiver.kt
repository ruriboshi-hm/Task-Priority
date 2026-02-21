package com.ruriboshi.taskpriority

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val channelId = "taskChannel"

class AlarmReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val titleExtra = intent.getStringExtra("titleExtra")
        val messageExtra = intent.getStringExtra("messageExtra")
        val notificationId = intent.getIntExtra("notificationId",0)
        val iT = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            iT,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification:Notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon_taskpriority)
            .setContentTitle(titleExtra)
            .setContentText(messageExtra)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId,notification)
    }

}