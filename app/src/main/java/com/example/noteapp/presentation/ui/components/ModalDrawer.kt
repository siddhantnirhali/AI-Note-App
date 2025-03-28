import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.noteapp.damain.model.DateTimeReminder
import com.example.noteapp.damain.model.Note
import com.example.noteapp.presentation.ui.components.NoteLabel
import com.example.noteapp.presentation.ui.components.NoteOptions
import com.example.noteapp.presentation.ui.components.ReminderContent

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDrawer(
    note: Note?,
    onDismissEvent: (Boolean) -> Unit,
    onNoteColorChangeRequest: (Color) -> Unit,
    onReminderStatusChanged: (dateTimeReminder: DateTimeReminder?) -> Unit,
    onNotelabelUpdated: (String, Boolean) -> Unit,
    onClearLabelPressed: () -> Unit,
    onDeleteNotePressed: () -> Unit,
    onMarkNoteFinishedNotePressed: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // Ensures full height expansion
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),  // Adaptive height
        onDismissRequest = { onDismissEvent(false) },
        sheetState = sheetState,
        dragHandle = null
    ) {
        when (selectedTab) {
            0 -> NoteOptions(note,
                onDismissEvent = { onDismissEvent(it) },
                onNoteColorChangeRequest = { onNoteColorChangeRequest(it) },
                onSetReminderRequest = { selectedTab = 1 },
                onDeleteNotePressed = { onDeleteNotePressed() },
                onLabelChangePressed = {selectedTab = 2},
                onMarkNoteFinishedNotePressed = { onMarkNoteFinishedNotePressed() })

            1 -> ReminderContent(
                note,
                onBackButtonPressed = { selectedTab = 0 },
                onExitPressed = {onDismissEvent(false)},
                onReminderStatusChanged = {onReminderStatusChanged(it)})

            2 -> NoteLabel(note, onLabelUpdated = onNotelabelUpdated, onClearLabelPressed = {onClearLabelPressed()}, onBackPressed = {selectedTab = 0}, onCancelPressed = {onDismissEvent(false)})
        }


        //CustomDatePicker()
    }
}








