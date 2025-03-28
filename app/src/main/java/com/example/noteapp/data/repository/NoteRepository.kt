package com.example.noteapp.data.repository

import com.example.noteapp.damain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun saveNote(note: Note)
    fun getAllNotes(): Flow<List<Note>>
    suspend fun deleteNote(note: Note)
}
