package com.example.noteapp.data.repository

import com.example.noteapp.damain.model.Note

interface GeminiRepository {

    suspend fun summarizeNote(note: Note?): String?
}