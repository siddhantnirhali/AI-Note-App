package com.example.noteapp.damain.usecase

import com.example.noteapp.damain.model.Note
import com.example.noteapp.data.repository.NoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) = repository.saveNote(note)
}
