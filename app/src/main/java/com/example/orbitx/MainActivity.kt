package com.example.orbitx

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.orbitx.Navigation.AppNavigation
import com.example.orbitx.Views.InstagramFeed
import com.example.orbitx.ui.theme.OrbitXTheme

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
