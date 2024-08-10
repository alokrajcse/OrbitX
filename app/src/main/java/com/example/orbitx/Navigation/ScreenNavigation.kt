package com.example.orbitx.Navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.orbitx.Views.MainChatScreen
import com.example.orbitx.Views.MainScreen
import com.example.orbitx.Views.SignInScreen
import com.example.orbitx.Views.SignUpScreen

@Composable
fun AppNavigation(activity: Activity) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            SignInScreen(
                onNavigateToHome = { navController.navigate("main") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            SignUpScreen(
                onNavigateBackToLogin = { navController.popBackStack() }
            )
        }
        composable("main") {
            MainScreen(activity = activity)
        }
        composable("MainChatScreen/{data}", arguments = listOf(navArgument("data") { type = NavType.StringType })) { backStackEntry ->
            val data = backStackEntry.arguments?.getString("data") ?: ""
            MainChatScreen(navController, data)
        }
    }
}