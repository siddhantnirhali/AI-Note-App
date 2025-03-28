package com.example.noteapp.service.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.noteapp.R

class ReminderReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ReminderReceiver", "🔥 Alarm triggered!")

        if (context == null || intent == null) {
            Log.e("ReminderReceiver", "Context or intent is null!")
            return
        }

        val title = intent.getStringExtra("noteTitle") ?: "Reminder"
        val content = intent.getStringExtra("noteContent") ?: "You have a reminder."

        Log.d("ReminderReceiver", "Notification Data -> Title: $title, Content: $content")

        showNotification(context, title, content)
    }

    private fun showNotification(context: Context, title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(1001, builder.build())

        Log.d("Reminder", "✅ Notification should be shown now!")
    }

}
