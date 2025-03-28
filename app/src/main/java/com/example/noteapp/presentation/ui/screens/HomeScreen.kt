package com.example.noteapp.presentation.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteapp.damain.model.Note
import com.example.noteapp.presentation.ui.components.DeleteNotesDialog
import com.example.noteapp.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val notes by viewModel.notes.collectAsState()
    val selectedNoteCount = notes.count { note -> note.isSelected }
    val isNoteSelected = notes.any { it.isSelected } // If any note is selected
    var isDeletePressed by remember { mutableStateOf(false) }
    Log.d("HomeScreen", notes.toString())
    Log.d("OnNoteSelected", "Note is Selected : $isNoteSelected")
    Scaffold(
        topBar = { TopBar(notes) },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                isNoteSelected = isNoteSelected,
                selectedNoteCount = selectedNoteCount,
                onTabSelected = { selectedTab = it },
                onCreateNoteClick = { viewModel.onNoteClicked(0) },
                onCancelSelection = { viewModel.onCancelSelection() },
                onFinishClicked = {
                    Toast.makeText(
                        context,
                        "Functionality not implemented yet...",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onDeleteClicked = { isDeletePressed = true }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            ContentSection(
                selectedTab = selectedTab,
                notes = notes,
                isDeletePressed = isDeletePressed,
                onDeleteConfirm = { viewModel.onDeletePressed()
                                 isDeletePressed = false },
                onDeleteCancel = {isDeletePressed = false},
                innerPadding = innerPadding,
                onNoteClicked = { viewModel.onNoteClicked(it.id) },
                onNoteSelected = {
                    viewModel.onNoteSelected(it.id)
                    Log.d("OnNoteSelected", "Note is Selected : True")
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(notes: List<Note>) {
    TopAppBar(
        modifier = Modifier.height(100.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF7D5BA6),
            titleContentColor = Color.White
        ),
        title = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Amazing Journey!", fontSize = 20.sp)
                Text(
                    text = "You have successfully finished ${notes.size} notes",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    )
}

@Composable
fun ContentSection(
    selectedTab: Int,
    notes: List<Note>,
    isDeletePressed: Boolean,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    innerPadding: PaddingValues,
    onNoteClicked: (Note) -> Unit,
    onNoteSelected: (Note) -> Unit
) {
    if(isDeletePressed){
        DeleteNotesDialog(onConfirm = {onDeleteConfirm()}, onDismiss = {onDeleteCancel()})
    }
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (selectedTab) {
            0 -> HomeContent(isDeletePressed, onDeleteConfirm= {onDeleteConfirm()}, onDeleteCancel = {onDeleteCancel()}, notes, onNoteClicked = { onNoteClicked(it) }, onNoteSelected = {
                onNoteSelected(it)
                Log.d("OnNoteSelected", "Note is Selected Invoked in ContentSection")
            })

            1 -> NotesGrid(notes, onNoteClicked = { onNoteClicked(it) }, onNoteSelected = {
                onNoteSelected(it)
                Log.d("OnNoteSelected", "Note is Selected Invoked in ContentSection")
            })

            2 -> Text("Search Screen", fontSize = 24.sp)
            3 -> Text("Settings Screen", fontSize = 24.sp)
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    isNoteSelected: Boolean,
    selectedNoteCount: Int,
    onTabSelected: (Int) -> Unit,
    onCreateNoteClick: () -> Unit,
    onCancelSelection: () -> Unit,
    onFinishClicked: () -> Unit,
    onDeleteClicked: () -> Unit

) {
    Log.d("OnNoteSelected", "Note is Selected in BottomNavigationBar : $isNoteSelected")

    if (isNoteSelected) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$selectedNoteCount Notes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(text = "Selected")
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onFinishClicked() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Finish",
                            tint = Color.Black
                        )
                        Text(text = "Finish", color = Color.Black)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onDeleteClicked() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                        Text(text = "Delete", color = Color.Black)
                    }

                }

            }

            Icon(
                Icons.Default.Close,
                contentDescription = "CancelSelection",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable { onCancelSelection() }
            )

        }

    } else {
        Box(modifier = Modifier.fillMaxWidth()) {
            NavigationBar(containerColor = Color.White) {
                val tabs = listOf(
                    Icons.Default.Home,
                    Icons.Default.Check,
                    Icons.Default.Search,
                    Icons.Default.Settings
                )
                val labels = listOf("Home", "Finished", "Search", "Settings")

                tabs.forEachIndexed { index, icon ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        icon = { Icon(icon, contentDescription = labels[index]) },
                        label = { Text(labels[index]) }
                    )
                }
            }

            FloatingActionButton(
                onClick = onCreateNoteClick,
                containerColor = Color(0xFF7D5BA6),
                shape = CircleShape,
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-32).dp)
                    .zIndex(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note", tint = Color.White)
            }
        }

    }


}

@Composable
fun NotesGrid(notes: List<Note>, onNoteClicked: (Note) -> Unit, onNoteSelected: (Note) -> Unit) {
    var isNoteSelected by remember { mutableStateOf(false) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes.size) { index ->
            NoteItem(note = notes[index], notes, onNoteClick = { onNoteClicked(it) }, onNoteSelected = {
                onNoteSelected(it)
                Log.d("OnNoteSelected", "Note is Selected Invoked in NotesGrid $isNoteSelected")
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(note: Note, notes: List<Note>,  onNoteClick: (Note) -> Unit, onNoteSelected: (Note) -> Unit) {
    val haptics = LocalHapticFeedback.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(250.dp)
            .then(
                if (note.isSelected) Modifier.border(
                    2.dp,
                    Color(0xFF7D5BA6),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .combinedClickable(
                onClick = { if(notes.any{it.isSelected})onNoteSelected(note) else onNoteClick(note) },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onNoteSelected(note)
                    Log.d("OnNoteSelected", "Note is Selected Invoked")
                },
                onLongClickLabel = "OnClick Note Selected"
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(note.noteBackgroundColor ?: Color.White.toArgb()))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Shopping",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = note.content)
        }
    }
}

@Composable
fun HomeContent(isDeletePressed: Boolean, onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, notes: List<Note>, onNoteClicked: (Note) -> Unit, onNoteSelected: (Note) -> Unit) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NoteCategorySection(
                "Pinned Notes",
                "There are not any pinned notes yet",
                notes.filter { it.isPinned },
                Modifier.weight(1f),
                onNoteClicked = { onNoteClicked(it) },
                onNoteSelected = {
                    onNoteSelected(it)
                    Log.d("OnNoteSelected", "Note is Selected Invoked in HomeContent ")
                })
            NoteCategorySection(
                "Interesting Ideas",
                "There is no notes yet",
                emptyList(),
                Modifier.weight(1f),
                onNoteClicked = { onNoteClicked(it) },
                onNoteSelected = { onNoteSelected(it) })
        }




}

@Composable
fun NoteCategorySection(
    title: String,
    emptyContent: String,
    notes: List<Note>,
    modifier: Modifier = Modifier,
    onNoteClicked: (Note) -> Unit,
    onNoteSelected: (Note) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if(notes.size > 3){
                Text(
                    text = "View all",
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { }
                )
            }

        }
        if(notes.isNotEmpty()){
            LazyHorizontalGrid(
                rows = GridCells.Fixed(1),
                modifier = Modifier.fillMaxWidth(), // Avoid fillMaxSize() to prevent extra height usage
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes.size) { index ->
                    NoteItem(
                        note = notes[index],
                        notes,
                        onNoteClick = { onNoteClicked(it) },
                        onNoteSelected = {
                            onNoteSelected(it)
                            Log.d("OnNoteSelected", "Note is Selected Invoked in NoteCategorySection")
                        })
                }
            }
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(emptyContent)
            }
        }

    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    //Sample List
    val dummyNotes = listOf(
        Note(
            id = 1,
            title = "Meeting Notes",
            content = "Discussed project roadmap and deadlines.",
            label = emptyList(),
            timestamp = 1708176000000
        ),
        Note(
            id = 2,
            title = "Grocery List",
            content = "Milk, Bread, Eggs, Butter, Cheese, Vegetables, Fruits",
            label = emptyList(),
            timestamp = 1708262400000
        ),
        Note(
            id = 3,
            title = "Workout Plan",
            content = "Monday: Chest & Triceps, Tuesday: Back & Biceps, Wednesday: Legs",
            label = emptyList(),
            timestamp = 1708348800000
        ),
        Note(
            id = 4,
            title = "Book Summary",
            content = "Summarized 'Atomic Habits' - Focus on small, consistent changes.",
            label = emptyList(),
            timestamp = 1708435200000
        ),
        Note(
            id = 5,
            title = "Daily Journal",
            content = "Had a productive day, completed two tasks from my to-do list.",
            label = emptyList(),
            timestamp = 1708521600000
        ),
        Note(
            id = 6,
            title = "Travel Plans",
            content = "Visiting Goa next weekend. Booked flights and hotels.",
            label = emptyList(),
            timestamp = 1708608000000
        ),
        Note(
            id = 7,
            title = "Recipe: Pasta",
            content = "Ingredients: Pasta, Tomatoes, Garlic, Basil, Olive Oil. Steps: Boil pasta, make sauce, mix.",
            label = emptyList(),
            timestamp = 1708694400000
        ),
        Note(
            id = 8,
            title = "Tech Ideas",
            content = "AI-powered note-taking app with voice recognition.",
            label = emptyList(),
            timestamp = 1708780800000
        ),
        Note(
            id = 9,
            title = "Learning Goals",
            content = "Complete Kotlin Coroutines and Jetpack Compose course.",
            label = emptyList(),
            timestamp = 1708867200000
        ),
        Note(
            id = 10,
            title = "Movie Watchlist",
            content = "Inception, Interstellar, The Dark Knight, The Matrix",
            label = emptyList(),
            timestamp = 1708953600000
        )
    )
    Scaffold(
        topBar = { TopBar(dummyNotes) },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = 0,
                false,
                1,
                onTabSelected = { },
                onCreateNoteClick = { },
                onCancelSelection = {},
                onFinishClicked = {},
                onDeleteClicked = {}
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding ->
            ContentSection(
                selectedTab = 0,
                notes = dummyNotes,
                isDeletePressed = false,
                onDeleteConfirm = {},
                onDeleteCancel = {},
                innerPadding = innerPadding,
                onNoteClicked = { },
                onNoteSelected = { }
            )
        }
    )
}




