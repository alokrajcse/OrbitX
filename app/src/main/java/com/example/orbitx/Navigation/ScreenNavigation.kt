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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(activity: Activity) {
    val navController = rememberNavController()
    val start = if(FirebaseAuth.getInstance().currentUser != null) "main" else "login"

    NavHost(navController = navController, startDestination = start) {
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