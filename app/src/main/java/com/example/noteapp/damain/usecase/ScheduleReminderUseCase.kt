package com.example.noteapp.damain.usecase

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.noteapp.core.utils.NotificationHelper
import com.example.noteapp.damain.model.Note
import com.example.noteapp.service.receiver.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(note: Note) {
        //requestExactAlarmPermission(context)
        if (note.dateTimeReminder != null) {
            NotificationHelper.scheduleNotification(context, note)
        }

    }

    fun cancel(note: Note){
        if (note.dateTimeReminder != null) {
            NotificationHelper.cancelNotification(context, note)
        }
    }

    private fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        }
    }
}

