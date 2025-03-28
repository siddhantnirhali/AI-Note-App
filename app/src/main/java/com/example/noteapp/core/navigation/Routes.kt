package com.example.noteapp.core.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object NoteDetail : Routes("noteDetail/{noteId}") {
        fun createRoute(noteId: Int) = "noteDetail/$noteId"
    }
    object Settings : Routes("settings")
}
