package com.example.noteapp.core.utils

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.example.noteapp.damain.model.DateTimeReminder

val DateTimeReminderSaver: Saver<DateTimeReminder, List<Any>> = object : Saver<DateTimeReminder, List<Any>> {
    override fun restore(value: List<Any>): DateTimeReminder {
        return DateTimeReminder(
            selectedDay = value[0] as Int,
            selectedMonth = value[1] as Int,
            selectedYear = value[2] as Int,
            selectedHour = value[3] as Int,
            selectedMinute = value[4] as Int,
            selectedReminder = value[5] as String
        )
    }

    override fun SaverScope.save(value: DateTimeReminder): List<Any> {
        return listOf(
            value.selectedDay,
            value.selectedMonth,
            value.selectedYear,
            value.selectedHour,
            value.selectedMinute,
            value.selectedReminder
        )
    }
}
