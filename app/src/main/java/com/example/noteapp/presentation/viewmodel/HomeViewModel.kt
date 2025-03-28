package com.example.noteapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.core.navigation.Routes
import com.example.noteapp.damain.model.Note
import com.example.noteapp.damain.usecase.GetNotesUseCase
import com.example.noteapp.core.navigation.NavigationProvider
import com.example.noteapp.damain.usecase.DeleteNotesUseCase
import com.example.noteapp.damain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val navigationProvider: NavigationProvider
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        viewModelScope.launch {
            getNotesUseCase().collect { _notes.value = it }
        }
    }

    fun onNoteClicked(noteId: Int) {
        navigationProvider.navigateTo(Routes.NoteDetail.createRoute(noteId))
    }

    fun onNoteSelected(noteId: Int){
        _notes.value = _notes.value.map { note ->
            if (note.id == noteId) note.copy(isSelected = !note.isSelected) else note
        }
    }

    fun onCancelSelection(){
        _notes.value = _notes.value.map { it.copy(isSelected = false) }
    }

    fun onDeletePressed(){
        viewModelScope.launch {
            val selectedNotes = _notes.value.filter { it.isSelected } // Get all selected notes
            selectedNotes.forEach { note ->
                deleteNotesUseCase(note) // Delete each selected note
                Log.d("NoteDetailViewModel", "Note deleted: ${note.id}")
            }

            // Update the list to remove deleted notes
            _notes.value = _notes.value.filter { !it.isSelected }
        }
    }

    fun onFinishPressed(){

    }
}