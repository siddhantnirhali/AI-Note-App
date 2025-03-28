package com.example.noteapp.data.repository

import android.util.Log
import com.example.noteapp.damain.model.Note
import com.example.noteapp.data.local.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun saveNote(note: Note) {
        noteDao.insertNote(note)
        Log.d("Room", "Saving $note data in local storage ")
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
        Log.d("Room", "Deleting $note data from local storage ")
    }
}
