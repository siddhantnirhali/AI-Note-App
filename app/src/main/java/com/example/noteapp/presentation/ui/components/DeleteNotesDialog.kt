package com.example.noteapp.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeleteNotesDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(12.dp),
        shape = RoundedCornerShape(24.dp), // More rounded corners
        containerColor = Color(0xFFF6EFFB), // Soft Purple Background
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete, // Use your custom trash icon
                    contentDescription = "Delete Icon",
                    tint = Color(0xFFE53935), // Red color
                    modifier = Modifier.size(50.dp) // Adjusted icon size
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Delete Notes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
        },
        text = {
            Text(
                text = "Are you sure you want to delete the selected notes?\n\n" +
                        "Deleting notes means it will be removed permanently.",
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp) // Space between buttons
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    border = BorderStroke(1.dp, Color(0xFF7D5BA6)), // Purple border
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f) // Equal width for both buttons
                ) {
                    Text("Cancel", color = Color(0xFF7D5BA6))
                }

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5BA6)), // Purple Button
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f) // Equal width for both buttons
                ) {
                    Text("Yes, Delete", color = Color.White)
                }
            }
        }
    )
}