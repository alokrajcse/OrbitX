package com.example.orbitx

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.orbitx.Navigation.AppNavigation
import com.example.orbitx.Navigation.BottomNavigationBar
import com.example.orbitx.Navigation.shouldShowBottomBar
import com.example.orbitx.Views.ChatHomeScreen
import com.example.orbitx.Views.CreatePostScreen
import com.example.orbitx.Views.Exit
import com.example.orbitx.Views.InstagramFeed
import com.example.orbitx.Views.MainChatScreen
import com.example.orbitx.Views.SearchScreen
import com.example.orbitx.ui.theme.OrbitXTheme
import com.example.orbitx.Views.SignInScreen
import com.example.orbitx.Views.SignUpScreen
import com.example.orbitx.Views.UserProfile
import com.example.orbitx.Views.UserProfile2
import com.example.orbitx.Views.myProfileScreen
import com.example.orbitx.Views.otherUserProfileSection


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrbitXTheme {
                AppNavigation(activity = this)
            }
        }
    }
}





@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val urbanistMedium = FontFamily(Font(R.font.urbanist_medium))
    var texttest by remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background( Brush.verticalGradient(listOf(Color(0xFFF85A4F), Color(0xFFE49E99))))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    Text(
                        text = "OrbitX",
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = urbanistMedium,
                        color = Color.Black
                    )

                    IconButton(onClick = { navController.navigate("chats") }) {
                        Image(
                            painter = painterResource(id = R.drawable.messagebutton),
                            contentDescription = "",
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(top=60.dp)) {

            InstagramFeed()
        }

    }
}
