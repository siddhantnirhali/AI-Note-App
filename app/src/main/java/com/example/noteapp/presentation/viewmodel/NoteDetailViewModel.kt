package com.example.noteapp.presentation.viewmodel

import android.content.Context
import com.example.noteapp.damain.usecase.ScheduleReminderUseCase
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.core.navigation.NavigationProvider
import com.example.noteapp.core.navigation.Routes
import com.example.noteapp.damain.model.DateTimeReminder
import com.example.noteapp.damain.model.Note
import com.example.noteapp.damain.usecase.DeleteNotesUseCase
import com.example.noteapp.damain.usecase.GetNotesUseCase
import com.example.noteapp.damain.usecase.SaveNoteUseCase
import com.example.noteapp.data.repository.GeminiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val saveNoteUseCase: SaveNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val geminiRepository: GeminiRepository,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val scheduleReminderUseCase: ScheduleReminderUseCase,
    private val navigationProvider: NavigationProvider
) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _summarizedtext = MutableStateFlow<String>("")
    val summarizedtext: StateFlow<String> = _summarizedtext

    var title = ""
    var content = ""
    var pinStatus = false

    fun fetchNoteDetails(noteId: Int) {
        viewModelScope.launch {
            getNotesUseCase().collect { notes ->
                _isLoading.value = true
                Log.d("NoteDetailViewModel", "Fetching details for noteId: $noteId")
                _note.value = notes.find { note -> note.id == noteId } ?: Note(
                    id = _note.value?.id ?: 0,
                    title = _note.value?.title ?: title,
                    content = _note.value?.content ?: content,
                    isPinned = _note.value?.isPinned ?: false,
                    isSelected = _note.value?.isSelected ?: false,
                    label = _note.value?.label ?: emptyList(),
                    timestamp = 0,
                )
                _isLoading.value = false
                Log.d("NoteDetailViewModel", "Note details fetched: ${_note.value}")
            }
        }
    }

    fun updateTitle(newTitle: String) {
        title = newTitle
        _note.value = _note.value?.copy(title = title)
        Log.d("NoteDetailViewModel", "Note title changed: ${title}")
    }

    fun updateContent(newContent: String) {
        content = newContent
        _note.value = _note.value?.copy(content = content)
        Log.d("NoteDetailViewModel", "Note content changed: $content")
    }

    fun updatePinStatus(newStatus: Boolean) {
        _note.value = _note.value?.copy(isPinned = newStatus)
        pinStatus = newStatus
        Log.d("PinStatus", "Pin Status: ${_note.value}")
    }

    fun updateNoteBackground(color: Color) {
        _note.value = _note.value?.copy(noteBackgroundColor = color.toArgb())
        Log.d("noteColor", "noteColor: ${_note.value?.noteBackgroundColor}")
    }

    fun saveNote() {
        val note = Note(
            id = _note.value?.id ?: 0,
            title = _note.value?.title ?: title,
            content = _note.value?.content ?: content,
            isPinned = _note.value?.isPinned ?: false,
            isSelected = _note.value?.isSelected ?: false,
            label = _note.value?.label ?: emptyList(),
            dateTimeReminder = _note.value?.dateTimeReminder,
            noteBackgroundColor = _note.value?.noteBackgroundColor ?: Color.White.toArgb()
        )

        if (note.content.isNotBlank() && note.title.isNotBlank()) {
            viewModelScope.launch {
                saveNoteUseCase(note)
                Log.d("NoteDetailViewModel", "Note content Saved for : ${note.id}")
            }
        }
    }

    fun updateNoteReminder(dateTimeReminder: DateTimeReminder?) {
        _note.value = _note.value?.copy(dateTimeReminder = dateTimeReminder)
        saveNote()

        _note.value?.let { note ->
            if (note.dateTimeReminder?.let { isReminderInvalid(it) } == true) {
                Log.e("NoteCheck", "Some fields are missing or invalid!")
                scheduleReminderUseCase.cancel(note)
            } else {
                Log.d("NoteCheck", "Note is valid!")
                scheduleReminderUseCase.schedule(note)
            }
        }
    }

    fun updateNoteLabel(label: String, isAdded: Boolean) {
        if (isAdded) {
            addLabel(label)
        } else {
            removeLabel(label)
        }
    }

    private fun addLabel(label: String) {
        val currentLabels: List<String> = _note.value?.label ?: emptyList()
        if (!currentLabels.contains(label)) {
            val updatedLabels = currentLabels + label
            _note.value = _note.value?.copy(label = updatedLabels)
        }

    }

    private fun removeLabel(label: String) {
        val currentLabels = _note.value?.label ?: emptyList()
        val updatedLabels = currentLabels.filter { it != label }
        _note.value = _note.value?.copy(label = updatedLabels)
    }

    fun clearNoteLabel() {
        _note.value = _note.value?.copy(label = emptyList())
    }

    fun deleteNote() {
        viewModelScope.launch {
            val note = _note.value
            if (note != null) {
                deleteNotesUseCase(note)
            }
        }
        navigationProvider.navigateTo(Routes.Home.route)
    }

    fun onBackClicked() {
        saveNote()
        navigationProvider.navigateTo(Routes.Home.route)
    }

    private fun isReminderInvalid(dateTimeReminder: DateTimeReminder): Boolean {
        return dateTimeReminder.let { // Check if DateTimeReminder is incomplete
            it.selectedDay == 0 ||
                    it.selectedMonth == 0 ||
                    it.selectedYear == 0 ||
                    it.selectedHour == 0 ||
                    it.selectedMinute == 0 ||
                    it.selectedReminder.isBlank()
        }
    }

    fun summarizeNote(context: Context): Boolean{
        val note = _note.value
        if (note != null) {
            if(note.content.isNotBlank()){
                viewModelScope.launch {
                    val text = geminiRepository.summarizeNote(note)
                    if (text != null) {
                        _summarizedtext.value = text
                    }
                    Log.d("Summarization", text.toString())
                }
                return true
            }
            else{
                Toast.makeText(context, "Content is empty to summarize", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        else{
            return false
        }

    }
}
