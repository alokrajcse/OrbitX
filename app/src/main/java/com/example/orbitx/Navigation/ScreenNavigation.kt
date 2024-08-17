package com.example.orbitx.Navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.orbitx.Views.EditProfileScreen
import com.example.orbitx.Views.MainChatScreen
import com.example.orbitx.Views.MainScreen
import com.example.orbitx.Views.SignInScreen
import com.example.orbitx.Views.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(activity: Activity, intent: Intent?) {
    val navController = rememberNavController()
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "main" else "login"



    NavHost(navController = navController, startDestination = startDestination) {
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

        composable("editprofile") {

            EditProfileScreen(navController)
        }

        composable("main") {
            MainScreen(activity = activity)
        }
        composable("MainChatScreen/{userUid}") { backStackEntry ->
            val userUid = backStackEntry.arguments?.getString("userUid") ?: ""
            MainChatScreen(navController, userUid)
        }
    }

    // Handle initial navigation based on intent
    intent?.let {
        val route = it.getStringExtra("EXTRA_SCREEN_ROUTE")
        val userUid = it.getStringExtra("uid")

        if (route != null) {
            val finalRoute = if (userUid != null) "$route/$userUid" else route
            navController.navigate(route)
        }
    }
}
