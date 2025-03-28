package com.example.noteapp.presentation.ui.screens


import NoteDrawer
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteapp.R
import com.example.noteapp.damain.model.DateTimeReminder
import com.example.noteapp.damain.model.Note
import com.example.noteapp.presentation.ui.components.CustomLabelChip
import com.example.noteapp.presentation.ui.components.DeleteNotesDialog
import com.example.noteapp.presentation.viewmodel.NoteDetailViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteDetailScreen(noteId: Int?, viewModel: NoteDetailViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val note = viewModel.note.collectAsState().value
    var showPanel by remember { mutableStateOf(false) }
    val summarizeNote = viewModel.summarizedtext.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value // New state to track loading

    LaunchedEffect(noteId) {
        viewModel.fetchNoteDetails(noteId ?: 0)
    }


    Scaffold(
        topBar = { TopBarNote(onBackPressed = { viewModel.onBackClicked() }) },

        bottomBar = {
            if (isLoading) {
                LoadingScreen() // Show a loading indicator while data is fetching
            } else {
                BottomBar(note = note, onPinClicked = {
                    viewModel.updatePinStatus(it)
                    Log.d("Pinned Status", viewModel.pinStatus.toString())
                },
                    onSummarizeNoteClicked = {
                        val response = viewModel.summarizeNote(context = context)
                        showPanel = response
                    },
                    onNoteColorChangeRequest = { viewModel.updateNoteBackground(it) },
                    onNoteLabelUpdated = { label, isAdded ->
                        viewModel.updateNoteLabel(
                            label,
                            isAdded
                        )
                    },
                    onClearLabelPressed = { viewModel.clearNoteLabel() },
                    onReminderStatusChanged = { viewModel.updateNoteReminder(it) },
                    onDeleteNotePressed = { viewModel.deleteNote() },
                    onMarkNoteFinishedNotePressed = { viewModel.saveNote() })
            }
        },
        floatingActionButton = { }, // FAB remains at the center
        floatingActionButtonPosition = FabPosition.Center, // Keep it centered
        content = { innerPadding ->
            if (isLoading) {
                LoadingScreen() // Show a loading indicator while data is fetching
            } else {
                NoteContent(
                    note = note,
                    showPanel = showPanel,
                    summarizeNote = summarizeNote,
                    innerPadding = innerPadding,
                    onNoteTitleChanged = { viewModel.updateTitle(it) },
                    onNoteContentChanged = { viewModel.updateContent(it) },
                    onSummarizePanelDismiss = { showPanel = false }
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun NoteDetailScreenPreview() {
    Scaffold(
        topBar = { TopBarNote(onBackPressed = {}) },
        bottomBar = {
            BottomBar(Note(0, "", ""), onPinClicked = { },
                onSummarizeNoteClicked = {},
                onNoteColorChangeRequest = {},
                onNoteLabelUpdated = { label, isAdded ->
                    Log.d("Preview", "Label: $label, isAdded: $isAdded")
                },
                onClearLabelPressed = {},
                onReminderStatusChanged = {},
                onDeleteNotePressed = {},
                onMarkNoteFinishedNotePressed = {})
        },
        floatingActionButton = { }, // FAB remains at the center
        floatingActionButtonPosition = FabPosition.Center, // Keep it centered
        content = { innerPadding ->
            NoteContent(
                note = null,
                showPanel = false,
                summarizeNote = "",
                innerPadding = innerPadding,
                onNoteTitleChanged = { },
                onNoteContentChanged = { },
                onSummarizePanelDismiss = {}
            )
        }
    )
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Background color for better visibility
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF7D5BA6), // Use your app's theme color
            strokeWidth = 4.dp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNote(onBackPressed: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF7D5BA6),
            titleContentColor = Color.White
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "BackButton",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onBackPressed() },
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Back", fontSize = 20.sp)
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun NoteContent(
    note: Note?,
    showPanel: Boolean,
    summarizeNote: String,
    innerPadding: PaddingValues,
    onNoteTitleChanged: (String) -> Unit,
    onNoteContentChanged: (String) -> Unit,
    onSummarizePanelDismiss: () -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isFocused = remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf(note?.title ?: "") }
    var noteContent by remember { mutableStateOf(note?.content ?: "") }
    val textStyle =
        TextStyle(fontSize = 18.sp, lineHeight = 22.sp) // Define text size and line height
    val maxLines = 5 // Set the number of lines you want
    val calculatedHeight = (textStyle.lineHeight.value * maxLines).dp // Calculate total height
    var isExceeded by remember { mutableStateOf(false) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Log.d("Room", "Fetched $note)");


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color(note?.noteBackgroundColor ?: Color.White.toArgb()))
    ) {

        TopOverlayPanel(showPanel, onDismiss = { onSummarizePanelDismiss() }, summarizeNote)
        TextField(
            value = noteTitle,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            onValueChange = {
                noteTitle = it
                onNoteTitleChanged(noteTitle)
            },
            placeholder = {
                Text(
                    "Title here",
                    color = Color.Gray,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        keyboardController?.show()
                    }
                },

            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )

        TextField(
            value = noteContent,
            onValueChange = {
                noteContent = it
                onNoteContentChanged(noteContent)
                textLayoutResult?.let {
                    isExceeded = it.lineCount > maxLines // Check if lines exceed max
                }
            },
            placeholder = { Text("Write your notes here...", color = Color.Gray) },
            modifier = Modifier
                .then(
                    if (isFocused.value) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier.height(calculatedHeight)
                    }
                )
                .padding(top = 8.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused.value = it.isFocused
                    if (it.isFocused) {
                        keyboardController?.show()
                    }
                },
            textStyle = TextStyle(color = Color.Black),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent, // Removes focus underline
                unfocusedIndicatorColor = Color.Transparent, // Removes unfocused underline
                disabledIndicatorColor = Color.Transparent, // Removes disabled underline
                errorIndicatorColor = Color.Transparent, // Removes error underline
                focusedContainerColor = Color.Transparent, // Removes background when focused
                unfocusedContainerColor = Color.Transparent, // Removes background when unfocused
                disabledContainerColor = Color.Transparent, // Removes background when disabled
                errorContainerColor = Color.Transparent // Removes background when error
            ),
        )

        if (note?.hasReminder == true) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                1.dp,
                Color.Gray.copy(alpha = 0.2f)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "Reminder set on ${note.dateTimeReminder?.selectedDay}/${note.dateTimeReminder?.selectedMonth}/${note.dateTimeReminder?.selectedYear}, ${note.dateTimeReminder?.selectedHour}:${note.dateTimeReminder?.selectedMinute}"
            )
        }
        if (note?.label?.size!! > 0) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                note.label.forEach { label ->
                    CustomLabelChip(label, isRemoveable = false)
                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertTimestampToTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm a") // "HH:mm" for 24-hour format
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomBar(
    note: Note?,
    onPinClicked: (Boolean) -> Unit,
    onSummarizeNoteClicked: () -> Unit,
    onNoteColorChangeRequest: (Color) -> Unit,
    onNoteLabelUpdated: (String, Boolean) -> Unit,
    onClearLabelPressed: () -> Unit,
    onReminderStatusChanged: (dateTimeReminder: DateTimeReminder?) -> Unit,
    onDeleteNotePressed: () -> Unit,
    onMarkNoteFinishedNotePressed: () -> Unit
) {
    var isDeletePressed by remember { mutableStateOf(false) }

    var isDeleteNotePressed by remember { mutableStateOf(false) }

    var pinStatus by remember { mutableStateOf(note?.isPinned ?: false) }

    val lastEdited = convertTimestampToTime(note?.timestamp ?: 0)


    var showBottomSheet by remember { mutableStateOf(false) }


    BottomAppBar(
        actions = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Last edited on $lastEdited",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(200.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onSummarizeNoteClicked() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.aisummarizationpng),
                            contentDescription = "Summarize",
                        )
                    }
                    IconButton(onClick = {
                        onPinClicked(
                            if (pinStatus) {
                                pinStatus = false
                                false
                            } else {
                                pinStatus = true
                                true
                            }
                        )
                    }) {
                        Icon(
                            if (pinStatus) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Localized description",
                        )
                    }
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Localized description",
                        )
                    }

                }

            }

        }
    )

    // Bottom Navigation Drawer (Modal Bottom Sheet)
    if (showBottomSheet) {
        NoteDrawer(
            note,
            onDismissEvent = { showBottomSheet = it },
            onNoteColorChangeRequest = { onNoteColorChangeRequest(it) },
            onReminderStatusChanged = { onReminderStatusChanged(it) },
            onNotelabelUpdated = onNoteLabelUpdated,
            onClearLabelPressed = { onClearLabelPressed() },
            onDeleteNotePressed = {
                showBottomSheet = false
                isDeletePressed = true
            },
            onMarkNoteFinishedNotePressed = {
                onMarkNoteFinishedNotePressed()
                showBottomSheet = false
            })
    }
    if (isDeletePressed) {
        DeleteNotesDialog(
            onDismiss = {
                isDeletePressed = false
                showBottomSheet = true
            },
            onConfirm = {
                onDeleteNotePressed()
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopOverlayPanel(showPanel: Boolean, onDismiss: () -> Unit, summarizeNote: String) {
    if (showPanel) {
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
                    Text(
                        text = "Summarized Note",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
            },
            text = {
                Text(
                    text = summarizeNote,
                    color = Color.Black
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(

                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7D5BA6)), // Purple Button
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Got it", color = Color.White)
                    }
                }

            }
        )
    }
}