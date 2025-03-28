package com.example.noteapp.data.repository

import com.example.noteapp.damain.model.Note
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeminiRepositoryImpl @Inject constructor(): GeminiRepository {

    // Vertex AI service and the generative model
    // Model that supports your use case
    private val generativeModel = Firebase.vertexAI.generativeModel("gemini-2.0-flash")

    override suspend fun summarizeNote(note: Note?): String? {
        return withContext(Dispatchers.IO) { // Ensuring it runs in the background
            try {
                val prompt = "\"Summarize the following notes into a brief summary of 2-3 sentences, focusing only on key points.\": ${note?.content}"
                val response = generativeModel.generateContent(prompt)

                response.text ?: "No summary available."
            } catch (e: Exception) {
                e.printStackTrace() // Log the error properly
                null
            }
        }
    }

}