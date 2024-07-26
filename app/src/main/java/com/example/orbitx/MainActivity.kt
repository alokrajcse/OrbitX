package com.example.orbitx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.orbitx.ui.theme.OrbitXTheme
import com.example.orbitx.views.SignInScreen
import com.example.orbitx.views.SignInTestScreen
import com.example.orbitx.views.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrbitXTheme {
                //SignUpScreen()
                SignInTestScreen()
            }
        }
    }
}
