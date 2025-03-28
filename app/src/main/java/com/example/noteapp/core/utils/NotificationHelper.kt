package com.example.noteapp.core.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.noteapp.damain.model.Note
import com.example.noteapp.service.receiver.ReminderReceiver
import java.util.Calendar

object NotificationHelper {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, note: Note) {
        val reminder = note.dateTimeReminder ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("Reminder", "Exact alarm permission not granted! Requesting...")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
                return
            }
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, reminder.selectedYear)
            set(Calendar.MONTH, reminder.selectedMonth - 1)
            set(Calendar.DAY_OF_MONTH, reminder.selectedDay)
            set(Calendar.HOUR_OF_DAY, reminder.selectedHour)
            set(Calendar.MINUTE, reminder.selectedMinute)
            set(Calendar.SECOND, 0)
        }

        val triggerTime = calendar.timeInMillis

        if (triggerTime <= System.currentTimeMillis()) {
            Log.e("Reminder", "Selected time is in the past! Not scheduling notification.")
            return
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("noteTitle", note.title)
            putExtra("noteContent", note.content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, note.id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Log.d("Reminder", "Scheduling notification for: $triggerTime (System time: ${System.currentTimeMillis()})")

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
    // ✅ Function to Cancel Notification
    fun cancelNotification(context: Context, note: Note) {
        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context, note.id.hashCode(), intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // ✅ Check if it exists
        )

        if (pendingIntent != null) { // ✅ If notification is scheduled
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent) // ❌ Cancel the scheduled notification
            pendingIntent.cancel() // ✅ Ensure PendingIntent is canceled
            Log.d("NotificationHelper", "✅ Notification canceled for note ID: ${note.id}")
        } else {
            Log.d("NotificationHelper", "❌ No notification found for note ID: ${note.id}")
        }
    }
}
