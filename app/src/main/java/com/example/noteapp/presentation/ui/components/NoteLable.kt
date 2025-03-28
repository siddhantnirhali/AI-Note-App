package com.example.noteapp.presentation.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapp.damain.model.Note


@Composable
fun NoteLabel(note: Note?, onLabelUpdated: (String, Boolean) -> Unit, onClearLabelPressed: () -> Unit, onBackPressed: () -> Unit, onCancelPressed: () -> Unit) {
    LabelContent(note, onLabelUpdated = onLabelUpdated, onClearLabelPressed = {onClearLabelPressed()}, onBackPressed = {onBackPressed()}, onCancelPressed ={onCancelPressed()})
}

@Composable
fun LabelContent(note: Note?, onLabelUpdated : (String, Boolean) -> Unit, onClearLabelPressed: () -> Unit, onBackPressed : () -> Unit, onCancelPressed : () -> Unit){
    var noteLabelValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Row(
                modifier = Modifier.clickable {onBackPressed()},
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")

                Text("Extras")
            }
            IconButton(onClick = { onCancelPressed() }) {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
        }
        Text("Label Name")
        OutlinedTextField(
            value = noteLabelValue,
            onValueChange = {noteLabelValue = it},
            placeholder = { Text("Example: Important, etc.") },
            label = {Text("Example: Important, etc.")},
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = {

                    if(noteLabelValue.isNotBlank()){
                        onLabelUpdated(noteLabelValue, true)
                        noteLabelValue = ""
                    }

                    Log.d("NoteLabel", "Label Successfully Created")
                    keyboardController?.hide() // Hide keyboard
                    focusManager.clearFocus() // Clear focus
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Text("Press \"Enter\" on keyboard to create another label.")
        LabelsContainer(note, onLabelRemoved = onLabelUpdated)

        if(note?.label?.isEmpty() == false){
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = Color.Black.copy(alpha = 0.2f))
            ClearAllButton(onClick = {onClearLabelPressed()})
        }

    }
}
@Composable
fun LabelsContainer(note: Note?, onLabelRemoved: (String, Boolean) -> Unit){
    val labels = note?.label?: emptyList()
    LabelChips(labels, onRemoveLabel = onLabelRemoved)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabelChips(
    labels: List<String>,
    onRemoveLabel: (String, Boolean) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            CustomLabelChip(label, onRemove = onRemoveLabel)
        }
    }
}
@Composable
fun CustomLabelChip(
    text: String,
    onRemove: (String, Boolean) -> Unit = { _, _ -> },
    isRemoveable : Boolean = true
) {
    Surface(
        color = Color.LightGray.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text, color = Color.Black, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            if(isRemoveable){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onRemove(text, false) }
                )
            }

        }
    }
}

@Composable
fun ClearAllButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50), // Fully rounded button
        border = BorderStroke(1.dp, Color(0xFF9C27B0)), // Purple border
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF9C27B0) // Purple text and icon
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete, // Trash icon
            contentDescription = "Clear",
            tint = Color(0xFF9C27B0), // Purple tint
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Clear All", fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun NoteLabelPreview() {
    LabelContent(onLabelUpdated = { label, isAdded ->
        Log.d("Preview", "Label: $label, isAdded: $isAdded")
    }, onClearLabelPressed = {},
        onBackPressed = {}, onCancelPressed = {},
        note = TODO()
    )
}