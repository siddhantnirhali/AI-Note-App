package com.example.noteapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.noteapp.damain.model.Note

@Composable
fun NoteOptions(note: Note?, onDismissEvent: (Boolean) -> Unit, onNoteColorChangeRequest: (Color) -> Unit, onSetReminderRequest: () -> Unit, onDeleteNotePressed: () -> Unit, onLabelChangePressed : () -> Unit, onMarkNoteFinishedNotePressed: () -> Unit){
    val colors = listOf(
        Color(0xFFFFFFFF), //White
        Color(0xFFF8C8DC), // Light Pink
        Color(0xFFEDE7F6), // Lavender
        Color(0xFFD4F8E8), // Mint Green
        Color(0xFFFFF59D), // Soft Yellow
        Color(0xFFF7F6D5)  // Beige
    )
    val noteColor = colors.find { color -> color.toArgb() == note?.noteBackgroundColor }
    var selectedColor by remember { mutableStateOf(noteColor) }
    val reminderStatus = note?.dateTimeReminder?.takeIf {
        it.selectedYear != 0 || it.selectedMonth != 0 || it.selectedDay != 0
    }?.let {
        "Change"
    } ?: "Not Set"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Enables scrolling if content is too long
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Close Button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            IconButton(onClick = { onDismissEvent(false) }) {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
        }

        // Background Color Selection
        Text("CHANGE BACKGROUND")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, CircleShape)
                        .border(
                            width = if (color == selectedColor) 2.dp else 0.dp,
                            color = if (color == selectedColor) Color.Gray else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {onNoteColorChangeRequest(color)
                            selectedColor = color }
                )
            }
        }

        Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))

        // Extras Section
        Text("EXTRAS")
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            SettingOption("Set Reminder", reminderStatus, Icons.Default.DateRange, true, onClick = {onSetReminderRequest()})
            SettingOption("Change Note Type", "Daily", Icons.Outlined.Edit, true, onClick = {})
            SettingOption("Give Label", "Work", Icons.Outlined.Add, true, onClick = {onLabelChangePressed()})
            SettingOption("Mark as Finished", "Work", Icons.Default.CheckCircle, false, onClick = {onMarkNoteFinishedNotePressed()})
        }

        Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))

        // Delete Note Option - Ensure visibility
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onDeleteNotePressed() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = "Delete Note")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Note", color = Color.Red)
        }
    }
}
// Reusable function for extra settings rows
@Composable
fun SettingOption(title: String, value: String, icon: ImageVector, isArrowVisible: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title)
        }
        if(isArrowVisible){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value)
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Arrow")
            }
        }

    }
}