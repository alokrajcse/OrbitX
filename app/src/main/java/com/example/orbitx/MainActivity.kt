package com.example.orbitx

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatbyme2.Screens.MainChatScreen
import com.example.chatbyme2.ui.ChatHomeScreen

import com.example.orbitx.Views.CreatePostScreen
import com.example.orbitx.Views.Exit
import com.example.orbitx.Views.SearchScreen
import com.example.orbitx.Views.UserProfile
import com.example.orbitx.ui.theme.OrbitXTheme
import com.example.orbitx.Views.SignInScreen
import com.example.orbitx.Views.SignUpScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrbitXTheme {
                AppNavigation(activity = this)
                //MainScreen()

            }
        }
    }
}

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
        composable("home") {
            HomeScreen(navController)
        }
        composable("main") {
            MainScreen(activity = activity)
        }
        composable("MainChatScreen/{data}", arguments = listOf(navArgument("data"){type=
            NavType.StringType}))
        { backStackEntry ->
            val data = backStackEntry.arguments?.getString("data") ?: ""
            MainChatScreen(navController, data)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))



    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.orange))){

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)) {
                Text(text = "OrbitX", modifier = Modifier
                    .padding(10.dp)
                    .weight(1f), fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily  = urbanistMedium,
                    color = Color.Black
                )


                IconButton(onClick = {navController.navigate("chats")  }) {
                    Image(painter = painterResource(id = R.drawable.messagebutton), contentDescription = "", modifier = Modifier.height(30.dp))
                }



            }



        }



    }) {
       // Text("Welcome to the Home Screen!", fontFamily = urbanistMedium)
    }
}


@Composable
fun MainScreen(activity: Activity) {


    val navController = rememberNavController()


    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavigationBar(navController, selectedItemIndex) {
                    selectedItemIndex = it
                }
            }
        }) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") {
                HomeScreen(navController)
            }
            composable("search") {
                SearchScreen(navController)
            }
            composable("newpost") {
                CreatePostScreen()
            }
            composable("profile/{data}", arguments = listOf(navArgument("data"){type=
                NavType.StringType}))
            { backStackEntry ->
                val data = backStackEntry.arguments?.getString("data") ?: ""
                UserProfile()
            }
            composable("logout") {
                Exit(activity = activity, navController = navController)
            }
            composable("chats") {
                ChatHomeScreen(navController)
            }

            composable("MainChatScreen/{data}", arguments = listOf(navArgument("data"){type=
                NavType.StringType}))
            { backStackEntry ->
                val data = backStackEntry.arguments?.getString("data") ?: ""
                MainChatScreen(navController, data)
            }


        }
    }

}

fun shouldShowBottomBar(currentRoute: String?): Boolean {

    val routesWithoutBottomBar = listOf("chats","MainChatScreen/{data}","logout")
    return !routesWithoutBottomBar.contains(currentRoute)
}


@Composable
fun BottomNavigationBar(navController: NavController, selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        BottomNavigationItem(
            title = "home",
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "search",
            selectedIcon = Icons.Default.Search,
            unselectedIcon = Icons.Outlined.Search,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "newpost",
            selectedIcon = Icons.Default.AddCircle,
            unselectedIcon = Icons.Outlined.AddCircle,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "profile",
            selectedIcon = Icons.Default.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "logout",
            selectedIcon = Icons.Default.ExitToApp,
            unselectedIcon = Icons.Outlined.ExitToApp,
            hasNews = false
        )
    )



    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = Modifier.height(80.dp), containerColor = colorResource(id = R.color.orange)) {

        items.forEachIndexed { index, item ->

            val isSelected = currentRoute == item.title

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    onItemSelected(index)
                    navController.navigate(item.title) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    BadgedBox(badge = {}) {
                        Icon(
                            imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = null
                        )
                    }
                },
                label = {})
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)