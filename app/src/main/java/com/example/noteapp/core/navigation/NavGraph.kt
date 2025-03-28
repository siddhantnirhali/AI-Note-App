package com.example.noteapp.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.noteapp.presentation.ui.screens.HomeScreen
import com.example.noteapp.presentation.ui.screens.NoteDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Home.route) {
        addNoteListScreen()
        addNoteDetailScreen()
    }
}

private fun NavGraphBuilder.addNoteListScreen() {
    composable(Routes.Home.route) {
        HomeScreen()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun NavGraphBuilder.addNoteDetailScreen() {
    composable(Routes.NoteDetail.route) { backStackEntry ->
        val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
        NoteDetailScreen(noteId)
    }
}
