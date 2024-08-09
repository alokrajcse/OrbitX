package com.example.createpost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.createpost.ui.Screens.CreatePostScreen
import com.example.createpost.ui.theme.CreatePostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CreatePostTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CreatePostScreen(modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}

