package com.example.noteapp.damain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isSelected: Boolean = false,
    val noteBackgroundColor: Int = Color.White.toArgb(),
    val dateTimeReminder: DateTimeReminder? = null,
    val label:List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
){
    val hasReminder: Boolean
        get() = dateTimeReminder?.let {
            it.selectedYear != 0 || it.selectedMonth != 0 || it.selectedDay != 0 ||
                    it.selectedHour != 0 || it.selectedMinute != 0 || it.selectedReminder.isNotBlank()
        } ?: false
}

data class DateTimeReminder(
    var selectedDay: Int,
    var selectedMonth: Int,
    var selectedYear: Int,
    var selectedHour: Int,
    var selectedMinute: Int,
    var selectedReminder: String
)