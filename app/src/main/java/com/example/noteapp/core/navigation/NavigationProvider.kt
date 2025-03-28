package com.example.noteapp.core.navigation

import androidx.navigation.NavHostController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationProvider @Inject constructor() {
    private lateinit var navController: NavHostController

    fun setNavController(controller: NavHostController) {
        this.navController = controller
    }

    fun navigateTo(route: String) {
        navController.navigate(route)
    }

    fun goBack() {
        navController.popBackStack()
    }
}
