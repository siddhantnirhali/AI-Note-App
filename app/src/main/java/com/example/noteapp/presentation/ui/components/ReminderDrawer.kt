package com.example.noteapp.presentation.ui.components

import CustomDatePicker
import SelectionList
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.noteapp.core.utils.DateTimeReminderSaver
import com.example.noteapp.damain.model.DateTimeReminder
import com.example.noteapp.damain.model.Note
import com.example.noteapp.damain.usecase.ScheduleReminderUseCase


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderContent(
    note: Note?,
    optionTitle: String = "Extras",
    onBackButtonPressed: () -> Unit,
    onExitPressed: () -> Unit,
    onReminderStatusChanged: (dateTimeReminder: DateTimeReminder?) -> Unit
) {

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedDateTime by rememberSaveable(stateSaver = DateTimeReminderSaver) {
        mutableStateOf(
            note?.dateTimeReminder ?: DateTimeReminder(
                0,
                selectedMonth = 0,
                selectedYear = 0,
                selectedHour = 0,
                selectedMinute = 0,
                selectedReminder = ""
            )
        )
    }

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Enables scrolling if content is too long
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Row(
                modifier = Modifier.clickable {
                    if (selectedTab == 0) onBackButtonPressed()
                    else selectedTab = 0
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")

                Text(optionTitle)
            }
            IconButton(onClick = { onExitPressed() }) {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
        }


        Divider(Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray.copy(alpha = 0.5f))


        Column(
            //modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            when (selectedTab) {
                0 -> Reminder(
                    note,
                    "Reminder",
                    "Not Set",
                    Icons.Default.DateRange,
                    onClick = { selectedTab = it },
                    onReminderStatusChanged = {
                        selectedDateTime = DateTimeReminder(
                            0,
                            selectedMonth = 0,
                            selectedYear = 0,
                            selectedHour = 0,
                            selectedMinute = 0,
                            selectedReminder = ""
                        )
                        onReminderStatusChanged(selectedDateTime)
                    })


                1 -> CustomDatePicker { day, month, year ->
                    selectedDateTime = selectedDateTime.copy(
                        selectedDay = day,
                        selectedMonth = month,
                        selectedYear = year
                    )
                    onReminderStatusChanged(selectedDateTime)
                }

                2 -> CustomTimePicker { hour, minute ->
                    selectedDateTime =
                        selectedDateTime.copy(selectedHour = hour, selectedMinute = minute)
                    onReminderStatusChanged(selectedDateTime)
                }

                3 -> SelectionList { reminderType ->
                    selectedDateTime = selectedDateTime.copy(selectedReminder = reminderType)
                    onReminderStatusChanged(selectedDateTime)
                }
            }
        }
    }
}

// Reusable function for extra settings rows
@Composable
fun Reminder(
    note: Note?,
    title: String,
    value: String,
    icon: ImageVector,
    onClick: (Int) -> Unit,
    onReminderStatusChanged: () -> Unit
) {
    var isReminderChecked by remember { mutableStateOf(note?.hasReminder ?: false) }

    val date = note?.dateTimeReminder?.takeIf {
        it.selectedYear != 0 || it.selectedMonth != 0 || it.selectedDay != 0
    }?.let {
        "${it.selectedDay}/${it.selectedMonth}/${it.selectedYear}"
    } ?: "Not Set"

    val time = note?.dateTimeReminder?.takeIf {
        it.selectedHour != 0 || it.selectedMinute != 0
    }?.let {
        "${it.selectedHour}:${it.selectedMinute}"
    } ?: "Not Set"

    val repeat = note?.dateTimeReminder?.selectedReminder?.takeIf { it.isNotBlank() } ?: "Not Set"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isReminderChecked) {
                Modifier.clickable { }

            } else {
                Modifier
            }),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title)
        }

        Switch(
            checked = isReminderChecked,
            onCheckedChange = {
                isReminderChecked = it
                onReminderStatusChanged()
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        )
    }
    ReminderOption(
        "Date",
        date,
        Icons.Default.DateRange,
        isReminderChecked,
        onClick = { onClick(1) })
    ReminderOption(
        "Time",
        time,
        Icons.Default.DateRange,
        isReminderChecked,
        onClick = { onClick(2) })
    ReminderOption(
        "Repeat",
        repeat,
        Icons.Default.DateRange,
        isReminderChecked,
        onClick = { onClick(3) })

}

@Composable
fun ReminderOption(
    title: String,
    value: String,
    icon: ImageVector,
    isToggleOn: Boolean,
    onClick: () -> Unit
) {

    var textColor by remember { mutableStateOf(Color.Gray) }
    if (isToggleOn) {
        textColor = Color.Black
    } else textColor = Color.Gray



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (isToggleOn) onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = textColor)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = textColor)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Arrow", tint = textColor)
        }
    }
}



